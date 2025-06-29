package com.n1netails.n1netails.api.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.n1netails.n1netails.api.exception.type.EmailExistException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.dto.PasskeySummary;
import com.n1netails.n1netails.api.model.dto.passkey.*;
import com.n1netails.n1netails.api.model.entity.PasskeyCredentialEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.UserRegisterRequest;
import com.n1netails.n1netails.api.repository.PasskeyCredentialRepository;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.repository.impl.YubicoCredentialRepositoryImpl;
import com.n1netails.n1netails.api.service.PasskeyService;
import com.n1netails.n1netails.api.service.UserService;
import com.n1netails.n1netails.api.util.JwtTokenUtil;
import com.yubico.webauthn.*;
import com.yubico.webauthn.data.*;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.yubico.webauthn.data.exception.Base64UrlException;
import com.yubico.webauthn.exception.AssertionFailedException;
import com.yubico.webauthn.exception.RegistrationFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.n1netails.n1netails.api.util.PasskeyUtil.generateFlowId;
import static com.n1netails.n1netails.api.util.PasskeyUtil.generateUserHandle;

@Service
@Slf4j
public class PasskeyServiceImpl implements PasskeyService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final RelyingParty relyingParty;
    private final Cache<String, PublicKeyCredentialCreationOptions> registrationCache;
    private final Cache<String, AssertionRequest > authenticationCache;
    private final PasskeyCredentialRepository passkeyCredentialRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public PasskeyServiceImpl(UserRepository userRepository,
                              UserService userService,
                              @Value("${n1netails.passkey.relying-party-id}") String rpId,
                              @Value("${n1netails.passkey.relying-party-name}") String rpName,
                              @Value("${n1netails.passkey.origins}") Set<String> origins,
                              PasskeyCredentialRepository passkeyCredentialRepository,
                              JwtTokenUtil jwtTokenUtil
    ) {
        this.userRepository = userRepository;
        this.userService = userService;

        RelyingPartyIdentity rpIdentity = RelyingPartyIdentity.builder()
                .id(rpId)
                .name(rpName)
                .build();

        // Configure the RelyingParty instance
        this.relyingParty = RelyingParty.builder()
                .identity(rpIdentity)
                .credentialRepository(new YubicoCredentialRepositoryImpl(passkeyCredentialRepository, userRepository))
                .origins(origins)
                .allowUntrustedAttestation(true)
                .allowOriginPort(true) // For localhost development
                .allowOriginSubdomain(true)
                .build();

        this.registrationCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
        this.authenticationCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .build();

        this.passkeyCredentialRepository = passkeyCredentialRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    // === REGISTRATION ===
    @Override
    public PasskeyRegistrationStartResponseDto startRegistration(PasskeyRegistrationStartRequestDto request)
            throws UserNotFoundException, Base64UrlException, EmailExistException {

        log.info("startRegistration user entity");
        UsersEntity user;
        Optional<UsersEntity> optionalUsersEntity = userRepository.findUserByEmail(request.getEmail());
        if (optionalUsersEntity.isPresent()) {
            user = optionalUsersEntity.get();
        } else {
// consider adding this part after user email is validated and user ownership of email is confirmed.
//            log.info("creating new user");
//            UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
//            userRegisterRequest.setEmail(request.getEmail());
//            userRegisterRequest.setUsername(request.getEmail().substring(0, request.getEmail().indexOf('@')));
//            user = this.userService.register(userRegisterRequest);
            throw new UserNotFoundException("The requested user does not exist.");
        }

        log.info("building user identity");
        // Build UserIdentity using staged builder
        UserIdentity userIdentity = UserIdentity.builder()
                .name(user.getEmail())
                .displayName(user.getFirstName() + " " + user.getLastName())
                .id(generateUserHandle(user))
                .build();

        log.info("building authenticator");
        // Build authenticator selection with builder
        AuthenticatorSelectionCriteria authenticatorSelection = AuthenticatorSelectionCriteria.builder()
                .residentKey(ResidentKeyRequirement.PREFERRED)
                .userVerification(UserVerificationRequirement.PREFERRED)
                .build();

        log.info("start registration options");
        // StartRegistrationOptions — no builder, uses constructor directly
        StartRegistrationOptions options = StartRegistrationOptions.builder()
                .user(userIdentity)
                .authenticatorSelection(authenticatorSelection) // optional; omit if not needed
                .extensions(RegistrationExtensionInputs.builder().build()) // required; empty OK
                .build();

        log.info("public key credential creations options");
        PublicKeyCredentialCreationOptions credentialCreationOptions = relyingParty.startRegistration(options);
        String flowId = generateFlowId();
        registrationCache.put(flowId, credentialCreationOptions);
        log.info("Registration cache populated for flowId: {}", flowId);
        log.info("Started passkey registration process for user with flowId: {}", flowId);
        return new PasskeyRegistrationStartResponseDto(flowId, credentialCreationOptions);
    }

    @Override
    @Transactional
    public boolean finishRegistration(PasskeyRegistrationFinishRequestDto request) throws UserNotFoundException {

        log.info("Attempting to finish passkey registration for flowId: {}", request.getFlowId());
        PublicKeyCredentialCreationOptions creationOptions = registrationCache.getIfPresent(request.getFlowId());
        if (creationOptions == null) {
            log.warn("Passkey registration flow ID {} not found or expired.", request.getFlowId());
            return false;
        }

        log.debug("Retrieved PublicKeyCredentialCreationOptions from cache for flowId: {}", request.getFlowId());
        try {
            RegistrationResult registrationResult = relyingParty.finishRegistration(FinishRegistrationOptions.builder()
                    .request(creationOptions)
                    .response(request.getCredential())
                    .build());

            log.info("Successfully processed finishRegistration with relying party for flowId: {}", request.getFlowId());

            // creationOptions.getUser().getName() is the email.
            // creationOptions.getUser().getId() is the user handle (ByteArray).
            UsersEntity user = userRepository.findUserByEmail(creationOptions.getUser().getName())
                 .orElseThrow(() -> {
                     log.error("User (email associated with user handle {}) not found during finish registration for flowId: {}. User handle from options: {}",
                             creationOptions.getUser().getId().getBase64Url(), request.getFlowId(), creationOptions.getUser().getId().getBase64Url());
                     return new UserNotFoundException("User associated with passkey registration options not found.");
                 });
            log.debug("User entity (username: {}) retrieved for user handle {} during finishRegistration.", user.getUsername(), creationOptions.getUser().getId().getBase64Url());

            PasskeyCredentialEntity passkeyCredential = new PasskeyCredentialEntity();
            passkeyCredential.setUser(user);
            passkeyCredential.setCredentialId(registrationResult.getKeyId().getId().getBytes());
            byte[] pkBytes = registrationResult.getPublicKeyCose().getBytes();
            passkeyCredential.setPublicKeyCose(pkBytes);

            passkeyCredential.setSignatureCount(registrationResult.getSignatureCount());
            passkeyCredential.setUserHandle(creationOptions.getUser().getId().getBytes());
            passkeyCredential.setAttestationType(getAttestationType(request.getCredential()));
            passkeyCredential.setRegisteredAt(new Date());
            passkeyCredential.setLastUsedAt(new Date());
            passkeyCredential.setBackupEligible(registrationResult.isBackupEligible());
            passkeyCredential.setBackupState(registrationResult.isBackedUp());
            passkeyCredential.setUvInitialized(registrationResult.isUserVerified());

            if (request.getFriendlyName() != null && !request.getFriendlyName().isBlank()) {
                passkeyCredential.setDeviceName(request.getFriendlyName());
            } else {
                 passkeyCredential.setDeviceName("Passkey");
            }

            Set<String> transports = request.getCredential().getResponse().getTransports()
                                     .stream()
                                     .map(AuthenticatorTransport::getId)
                                     .collect(Collectors.toSet());
            passkeyCredential.setTransports(transports);

            Optional<AttestedCredentialData> attData =
                    request.getCredential().getResponse()
                            .getAttestation()
                            .getAuthenticatorData().getAttestedCredentialData();

            attData.ifPresent(ad -> passkeyCredential.setAaguid(UUID.nameUUIDFromBytes(ad.getAaguid().getBytes())));

            log.info("Attempting to save new passkey credential for user: {}", user.getUsername());
            passkeyCredentialRepository.savePasskeyCredential(passkeyCredential);
            registrationCache.invalidate(request.getFlowId());
            log.info("Successfully registered passkey for user: {}. Credential ID (first 8 bytes): {}", user.getUsername(),
                    Base64.getEncoder().encodeToString(Arrays.copyOf(passkeyCredential.getCredentialId(), 8)));
            return true;
        } catch (RegistrationFailedException e) {
            registrationCache.invalidate(request.getFlowId());
            log.error("Passkey registration failed for user with handle: {}. FlowId: {}. Reason: {}",
                    creationOptions.getUser().getDisplayName(), request.getFlowId(), e.getMessage(), e);
            return false;
        }
    }

    private String getAttestationType(PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> credential) {
        log.info("getAttestationType");
        try {
            return credential.getResponse().getAttestation().getFormat();
        } catch (Exception e) {
            log.warn("Could not determine attestation format: {}", e.getMessage());
            return "unknown";
        }
    }

    // === AUTHENTICATION ===
    @Override
    public PasskeyAuthenticationStartResponseDto startAuthentication(PasskeyAuthenticationStartRequestDto request) {
        log.info("startAuthentication");
        StartAssertionOptions options;

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            // Use builder with mandatory `username` i.e user email
            options = StartAssertionOptions.builder()
                    .username(request.getEmail())
                    .userVerification(UserVerificationRequirement.PREFERRED)
                    .build();
        } else {
            // Use builder without username (means allowDiscoverableCredentials = true)
            options = StartAssertionOptions.builder()
                    .userVerification(UserVerificationRequirement.PREFERRED)
                    .build();
        }

        String flowId = generateFlowId();
        AssertionRequest assertionRequest = relyingParty.startAssertion(options);
        authenticationCache.put(flowId, assertionRequest);

        PublicKeyCredentialRequestOptions requestOptions = assertionRequest.getPublicKeyCredentialRequestOptions();
        String userHint = request.getEmail() != null && !request.getEmail().isBlank() ? "email hint provided" : "discoverable credential";
        log.info("Started passkey authentication. FlowId: {}. User hint: {}", flowId, userHint);
        return new PasskeyAuthenticationStartResponseDto(flowId, requestOptions);
    }

    @Override
    @Transactional
    public PasskeyAuthenticationResponseDto finishAuthentication(PasskeyAuthenticationFinishRequestDto request) {
        log.info("Attempting to finish passkey authentication for flowId: {}", request.getFlowId());

        AssertionRequest assertionRequestOptions = authenticationCache.getIfPresent(request.getFlowId());
        if (assertionRequestOptions == null) {
            log.warn("Passkey authentication flow ID {} not found or expired.", request.getFlowId());
            return new PasskeyAuthenticationResponseDto(false, "Authentication flow expired or invalid.");
        }

        try {
            log.debug("Calling relyingParty.finishAssertion for flowId: {}", request.getFlowId());
            AssertionResult assertionResult = relyingParty.finishAssertion(FinishAssertionOptions.builder()
                    .request(assertionRequestOptions)
                    .response(request.getCredential())
                    .build());

            if (assertionResult.isSuccess()) {
                log.info("Passkey assertion successful for user handle: {}, credentialId (first 8 bytes): {}",
                        assertionResult.getUserHandle().getBase64Url(), Base64.getEncoder().encodeToString(Arrays.copyOf(assertionResult.getCredentialId().getBytes(), 8)));

                Optional<PasskeySummary> optionalPasskeySummary = passkeyCredentialRepository.findPasskeyByCredentialId(assertionResult.getCredentialId().getBytes());

                if(optionalPasskeySummary.isPresent()){
                    PasskeySummary cred = optionalPasskeySummary.get();
                    cred.setSignatureCount(assertionResult.getSignatureCount());
                    cred.setLastUsedAt(new Date());
                    log.debug("Updating signature count and last used time for credentialId (first 8 bytes): {}", Base64.getEncoder().encodeToString(Arrays.copyOf(cred.getCredentialId(), 8)));
                    passkeyCredentialRepository.updatePasskeySummary(cred);
                } else {
                     log.error("Passkey credential (first 8 bytes: {}) not found for signature count update. User handle from assertion: {}",
                             Base64.getEncoder().encodeToString(Arrays.copyOf(assertionResult.getCredentialId().getBytes(), 8)), assertionResult.getUserHandle().getBase64Url());
                }

                log.debug("Attempting to find user by user handle from assertion: {}", assertionResult.getUserHandle().getBase64Url());
                // assertionResult.getUsername() is the email. Use userHandle to find user if possible, or log username if it's not email.
                // For now, we retrieve by email (username from assertion) then log the system username.
                UsersEntity user = userRepository.findUserByEmail(assertionResult.getUsername()).orElseThrow(
                        () -> {
                            log.error("User (associated with user handle {}) not found in database using email from assertion. FlowId: {}",
                                    assertionResult.getUserHandle().getBase64Url(), request.getFlowId());
                            return new UserNotFoundException("Authenticated user not found.");
                        }
                );
                log.info("User {} (associated with user handle {}) found. Updating last login date.", user.getUsername(), assertionResult.getUserHandle().getBase64Url());

                user.setLastLoginDateDisplay(new Date());
                user.setLastLoginDate(new Date());
                user = userRepository.save(user);
                log.debug("User {} last login date updated.", user.getUsername());

                UserPrincipal userPrincipal = new UserPrincipal(user);
                String jwtToken = jwtTokenUtil.createToken(userPrincipal);
                authenticationCache.invalidate(request.getFlowId());

                UsersEntity passkeyUser = userRepository.findUserByEmail(user.getEmail()).orElseThrow(() -> new UserNotFoundException("User not located by email for passkey post-authentication."));
                log.info("Successfully authenticated user {} with passkey. FlowId: {}", user.getUsername(), request.getFlowId());
                return new PasskeyAuthenticationResponseDto(true, "Authentication successful", jwtToken, passkeyUser);
            } else {
                authenticationCache.invalidate(request.getFlowId());
                log.warn("Passkey assertion failed for flowId: {}. User handle from assertion (if available): {}",
                        request.getFlowId(), assertionResult.getUserHandle() != null ? assertionResult.getUserHandle().getBase64Url() : "N/A");
                return new PasskeyAuthenticationResponseDto(false, "Authentication failed.");
            }
        } catch (AssertionFailedException | UserNotFoundException e) {
            authenticationCache.invalidate(request.getFlowId());
            String credentialIdExtract = "N/A";
            if (request.getCredential() != null && request.getCredential().getId() != null) {
                try {
                    credentialIdExtract = Base64.getEncoder().encodeToString(Arrays.copyOf(request.getCredential().getId().getBytes(), 8));
                } catch (Exception ignored) {}
            }
            log.error("Passkey assertion failed for flowId: {}. Credential ID (first 8 bytes, if available): {}. Message: {}",
                    request.getFlowId(), credentialIdExtract, e.getMessage(), e);
            return new PasskeyAuthenticationResponseDto(false, "Authentication failed: " + e.getMessage());
        }
    }
}



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
        log.info("registration cache flow id: {}", flowId);
        log.info("Started passkey registration for user: {}, flowId: {}", user.getUsername(), flowId);
        return new PasskeyRegistrationStartResponseDto(flowId, credentialCreationOptions);
    }

    @Override
    @Transactional
    public boolean finishRegistration(PasskeyRegistrationFinishRequestDto request) throws UserNotFoundException {

        log.info("finish registration public key credentials creation options");
        PublicKeyCredentialCreationOptions creationOptions = registrationCache.getIfPresent(request.getFlowId());
        if (creationOptions == null) {
            log.warn("Passkey registration flow ID {} not found or expired.", request.getFlowId());
            return false; // todo throw specific exception
        }
        // Do NOT invalidate from cache immediately, finishRegistration might need it if it internally calls credentialRepository methods that check the cache

        log.info("RETRIEVING REGISTRATION RESULT");
        try {
            RegistrationResult registrationResult = relyingParty.finishRegistration(FinishRegistrationOptions.builder()
                    .request(creationOptions)
                    .response(request.getCredential())
                    .build());

            log.info("extracting user entity");
            // User with email should exist as it was checked in startRegistration
            UsersEntity user = userRepository.findUserByEmail(creationOptions.getUser().getName())
                 .orElseThrow(() -> new UserNotFoundException("User " + creationOptions.getUser().getName() + " not found during finish registration."));


            log.info("passkey credential entity");
            PasskeyCredentialEntity passkeyCredential = new PasskeyCredentialEntity();
            passkeyCredential.setUser(user);
            passkeyCredential.setCredentialId(registrationResult.getKeyId().getId().getBytes());
            log.info("REGISTRATION RESULT PUBLIC KEY COSE: {}", registrationResult.getPublicKeyCose().getBytes());
            byte[] pkBytes = registrationResult.getPublicKeyCose().getBytes();
            passkeyCredential.setPublicKeyCose(pkBytes);

            passkeyCredential.setSignatureCount(registrationResult.getSignatureCount());
            passkeyCredential.setUserHandle(creationOptions.getUser().getId().getBytes()); // Store the user handle used during registration
            passkeyCredential.setAttestationType(getAttestationType(request.getCredential())); // Helper to extract
            passkeyCredential.setRegisteredAt(new Date());
            passkeyCredential.setLastUsedAt(new Date()); // Set last used to now
            passkeyCredential.setBackupEligible(registrationResult.isBackupEligible());
            passkeyCredential.setBackupState(registrationResult.isBackedUp());

            // User Verification (uvInitialized) can be tricky.
            // The client indicates if UV was performed. The authenticator data flag (UV) reflects this.
            // `registrationResult.isUserVerified()` reflects the UV flag from authenticator data.
            passkeyCredential.setUvInitialized(registrationResult.isUserVerified());


            if (request.getFriendlyName() != null && !request.getFriendlyName().isBlank()) {
                passkeyCredential.setDeviceName(request.getFriendlyName());
            } else {
                 passkeyCredential.setDeviceName("Passkey"); // Default name
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

            log.info("ATTEMPTING PASSKEY CREDENTIAL REPOSITORY SAVE");
            passkeyCredentialRepository.savePasskeyCredential(passkeyCredential);
            registrationCache.invalidate(request.getFlowId()); // Invalidate after successful save
            log.info("Successfully registered passkey for user: {}, credentialId: {}", user.getUsername(), passkeyCredential.getCredentialId());
            return true;
        } catch (RegistrationFailedException e) {
            registrationCache.invalidate(request.getFlowId()); // Invalidate on failure too
            log.error("Passkey registration failed for user: {}. Reason: {}", creationOptions.getUser().getName(), e.getMessage(), e);
            return false; // Or throw specific exception
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
        log.info("Start Assertion Request: {}", assertionRequest);

        PublicKeyCredentialRequestOptions credentialCreationOptions = assertionRequest.getPublicKeyCredentialRequestOptions();

        log.info("Started passkey authentication, flowId: {}. For user (if provided): {}", flowId, request.getEmail());
        return new PasskeyAuthenticationStartResponseDto(flowId, credentialCreationOptions);
    }

    @Override
    @Transactional
    public PasskeyAuthenticationResponseDto finishAuthentication(PasskeyAuthenticationFinishRequestDto request) {
        log.info("finish authentication");
        log.info("flow id: {}", request.getFlowId());
        log.info("credential: {}", request.getCredential());

        request.setCredential(request.getCredential());
        AssertionRequest requestOptions = authenticationCache.getIfPresent(request.getFlowId());
        log.info("Finish Assertion Request: {}", requestOptions);
        if (requestOptions == null) {
            log.warn("Passkey authentication flow ID {} not found or expired.", request.getFlowId());
            return new PasskeyAuthenticationResponseDto(false, "Authentication flow expired or invalid.");
        }
        // Do NOT invalidate from cache immediately for the same reasons as registration

        try {
            log.info("finish authentication assertion result");
            AssertionResult assertionResult = relyingParty.finishAssertion(FinishAssertionOptions.builder()
                    .request(requestOptions)
                    .response(request.getCredential())
                    .build());

            if (assertionResult.isSuccess()) {
                log.info("finish authentication assertion result success");
                // IMPORTANT: Update the signature count in the database
                // The JpaCredentialRepository's updateSignatureCount method will be called by the library if configured correctly.
                // If not, or for belt-and-suspenders, ensure it's done:
                log.info("finish authentication assertion result findByCredentialId");
                log.info("credential: {}", assertionResult.getCredentialId());
                log.info("credential base 64: {}", assertionResult.getCredentialId().getBase64Url());
                Optional<PasskeySummary> optionalPasskeySummary = passkeyCredentialRepository.findPasskeyByCredentialId(assertionResult.getCredentialId().getBytes());

                if(optionalPasskeySummary.isPresent()){
                    PasskeySummary cred = optionalPasskeySummary.get();
                    cred.setSignatureCount(assertionResult.getSignatureCount());
                    cred.setLastUsedAt(new Date());
                    log.info("saving passkey credential");
                    passkeyCredentialRepository.updatePasskeySummary(cred);
                } else {
                     log.error("Passkey credential {} not found during finishAuthentication for signature count update.", assertionResult.getCredentialId().getBase64Url());
                }

                log.info("finish authentication assertion result UsersEntity");
                log.info("assertion result username: {}", assertionResult.getUsername());
                UsersEntity user = userRepository.findUserByEmail(assertionResult.getUsername()).orElseThrow(
                        () -> new UserNotFoundException("user not found by email for passkey")
                );
                if (user == null) {
                    // This can happen if the username from assertion doesn't match any user.
                    // Or if the user was deleted between start and finish.
                    log.error("User {} from passkey assertion not found in database.", assertionResult.getUsername());
                    authenticationCache.invalidate(request.getFlowId());
                    return new PasskeyAuthenticationResponseDto(false, "Authenticated user not found.");
                }

                // Update user's last login date
                user.setLastLoginDateDisplay(new Date());
                user.setLastLoginDate(new Date());
                log.info("saving user");
                user = userRepository.save(user);

                UserPrincipal userPrincipal = new UserPrincipal(user);

                // Manually create an Authentication object for the SecurityContext
                // This mimics what Spring Security would do for a password login
                // Note: For passkeys, authorities are usually derived from the user's existing roles, not the passkey itself.
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userPrincipal, null, userPrincipal.getAuthorities());

                String jwtToken = jwtTokenUtil.createToken(userPrincipal);
                authenticationCache.invalidate(request.getFlowId());

                UsersEntity passkeyUser = userRepository.findUserByEmail(user.getEmail()).orElseThrow(() -> new UserNotFoundException("User not located by email for passkey"));
                log.info("Successfully authenticated user {} with passkey credentialId: {}", user.getUsername(), assertionResult.getCredentialId().getBase64Url());
                return new PasskeyAuthenticationResponseDto(true, "Authentication successful", jwtToken, passkeyUser);
            } else {
                log.info("finish authentication assertion result fail");
                authenticationCache.invalidate(request.getFlowId());
                log.warn("Passkey assertion failed for credentialId: {}. Username from assertion: {}", request.getCredential().getId().getBase64Url(), assertionResult.getUsername());
                return new PasskeyAuthenticationResponseDto(false, "Authentication failed.");
            }
        } catch (AssertionFailedException | UserNotFoundException e) {
            authenticationCache.invalidate(request.getFlowId());
            log.error("Passkey assertion failed. Credential ID: {}. Message: {}", request.getCredential().getId().getBase64Url(), e.getMessage(), e);
            return new PasskeyAuthenticationResponseDto(false, "Authentication failed: " + e.getMessage());
        }
    }
}



package com.n1netails.n1netails.api.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.n1netails.n1netails.api.exception.type.EmailExistException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException; // Added this import
import com.n1netails.n1netails.api.model.dto.passkey.*;
import com.n1netails.n1netails.api.model.entity.PasskeyCredentialEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.UserRegisterRequest;
import com.n1netails.n1netails.api.repository.PasskeyCredentialRepository;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.yubico.webauthn.*;
import com.yubico.webauthn.data.*;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.yubico.webauthn.data.exception.Base64UrlException;
import com.yubico.webauthn.exception.AssertionFailedException;
import com.yubico.webauthn.exception.RegistrationFailedException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.Base64;

import static com.n1netails.n1netails.api.constant.ProjectSecurityConstant.EXPIRATION_TIME;

@Service
@Slf4j
public class PasskeyService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasskeyCredentialRepository passkeyCredentialRepository;
    private final RelyingParty relyingParty;
    private final Cache<String, PublicKeyCredentialCreationOptions> registrationCache;
    private final Cache<String, AssertionRequest > authenticationCache;
    private final JwtEncoder jwtEncoder;
    private final JdbcTemplate jdbcTemplate;

    private final SecureRandom random = new SecureRandom();

    @Autowired
    public PasskeyService(UserRepository userRepository,
                          UserService userService,
                          PasskeyCredentialRepository passkeyCredentialRepository,
                          @Value("${n1netails.passkey.relying-party-id}") String rpId,
                          @Value("${n1netails.passkey.relying-party-name}") String rpName,
                          @Value("${n1netails.passkey.origins}") Set<String> origins,
                          JwtEncoder jwtEncoder,
                          AuthenticationManager authenticationManager,
                          JdbcTemplate jdbcTemplate
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passkeyCredentialRepository = passkeyCredentialRepository;
        this.jwtEncoder = jwtEncoder;

        RelyingPartyIdentity rpIdentity = RelyingPartyIdentity.builder()
                .id(rpId)
                .name(rpName)
                .build();

        // Configure the RelyingParty instance
        this.relyingParty = RelyingParty.builder()
                .identity(rpIdentity)
                // todo consider refactoring the jpa centrail repository into it's own file
                .credentialRepository(new JpaCredentialRepository())
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

        this.jdbcTemplate = jdbcTemplate;
    }

    // === REGISTRATION ===
    public PasskeyRegistrationStartResponseDto startRegistration(PasskeyRegistrationStartRequestDto request)
            throws UserNotFoundException, Base64UrlException, EmailExistException {

        log.info("startRegistration user entity");
//        UsersEntity user = userRepository.findUserByEmail(request.getEmail())
//                .orElseThrow(() -> new UserNotFoundException("User not found: " + request.getEmail()));

        UsersEntity user;
        Optional<UsersEntity> optionalUsersEntity = userRepository.findUserByEmail(request.getEmail());
        if (optionalUsersEntity.isPresent()) {
            user = optionalUsersEntity.get();
        } else {
            log.info("creating new user");
            UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
            userRegisterRequest.setEmail(request.getEmail());
            userRegisterRequest.setUsername(request.getEmail().substring(0, request.getEmail().indexOf('@')));
            user = this.userService.register(userRegisterRequest);
        }

        log.info("building user identity");
        // Build UserIdentity using staged builder
        UserIdentity userIdentity = UserIdentity.builder()
                .name(user.getEmail())
                .displayName(user.getFirstName() + " " + user.getLastName())
                .id(generateUserHandle(user))
                .build();

        log.info("building exclude credentials");
        // Build excludeCredentials using correct builder
        List<PublicKeyCredentialDescriptor> excludeCredentials = passkeyCredentialRepository.findAllByUser(user).stream()
                .map(cred -> PublicKeyCredentialDescriptor.builder()
                        .id(new ByteArray(cred.getCredentialId()))
                        .build())
                .toList();

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
            this.savePasskeyCredentialManually(passkeyCredential);
            registrationCache.invalidate(request.getFlowId()); // Invalidate after successful save
            log.info("Successfully registered passkey for user: {}, credentialId: {}", user.getUsername(), passkeyCredential.getCredentialId());
            return true;
        } catch (RegistrationFailedException e) {
            registrationCache.invalidate(request.getFlowId()); // Invalidate on failure too
            log.error("Passkey registration failed for user: {}. Reason: {}", creationOptions.getUser().getName(), e.getMessage(), e);
            return false; // Or throw specific exception
        }
    }

    @Transactional
    public void savePasskeyCredentialManually(PasskeyCredentialEntity p) {
        log.info("savePasskeyCredentialManually");
        String sql = "INSERT INTO ntail.passkey_credentials " +
                "(aaguid, attestation_object, attestation_type, backup_eligible, backup_state, credential_id, device_name, last_used_at, public_key_cose, registered_at, signature_count, user_id, user_handle, uv_initialized) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        Long generatedId = jdbcTemplate.queryForObject(sql, Long.class,
                p.getAaguid(),
                p.getAttestationObject(),
                p.getAttestationType(),
                p.getBackupEligible(),
                p.getBackupState(),
                p.getCredentialId(),
                p.getDeviceName(),
                p.getLastUsedAt(),
                p.getPublicKeyCose(),
                p.getRegisteredAt(),
                p.getSignatureCount(),
                p.getUser().getId(),
                p.getUserHandle(),
                p.getUvInitialized()
        );

        p.setId(generatedId);
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
                Optional<PasskeySummary> optionalPasskeySummary = this.findPasskeyByCredentialId(assertionResult.getCredentialId().getBytes());

                if(optionalPasskeySummary.isPresent()){
                    PasskeySummary cred = optionalPasskeySummary.get();
                    cred.setSignatureCount(assertionResult.getSignatureCount());
                    cred.setLastUsedAt(new Date());
                    log.info("saving passkey credential");
                    this.savePasskeySummary(cred);
                }
                else {
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
                user.setLastLoginDateDisplay(user.getLastLoginDate());
                user.setLastLoginDate(new Date());
                log.info("saving user");
                user = userRepository.save(user);

                UserPrincipal userPrincipal = new UserPrincipal(user);

                // Manually create an Authentication object for the SecurityContext
                // This mimics what Spring Security would do for a password login
                // Note: For passkeys, authorities are usually derived from the user's existing roles, not the passkey itself.
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userPrincipal, null, userPrincipal.getAuthorities());

                String jwtToken = createToken(userPrincipal);
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

    // todo move to more common area
    private String createToken(UserPrincipal userPrincipal) {
        log.info("== createToken");
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("self") // TODO: Make configurable
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(EXPIRATION_TIME)) // EXPIRATION_TIME from constants
                .subject(userPrincipal.getUsername()) // email
                .claim("authorities", userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .claim("userId", ((UsersEntity) userPrincipal.getUser()).getUserId()) // Add userId to token
                .build();

        JwtEncoderParameters parameters = JwtEncoderParameters.from(claimsSet);
        log.info("getting JWT TOKEN: {}", jwtEncoder.encode(parameters).getTokenValue());
        return jwtEncoder.encode(parameters).getTokenValue();
    }


    // === UTILITY METHODS ===
    private @NonNull ByteArray generateUserHandle(UsersEntity user) throws Base64UrlException {
        log.info("== generateUserHandle");
        // User handle MUST be stable and unique for the user.
        // It should NOT be PII if possible, and MUST NOT change for a given user.
        // The Yubico library expects a ByteArray. Max 64 bytes.
        // Ensure it's at least 1 byte, preferably more for uniqueness.
        Long userId = user.getId();
        if (userId == null) {
            // This should not happen for an existing user
            log.error("User ID is null or blank for user: {}. Cannot generate user handle.", user.getUsername());
            throw new IllegalArgumentException("User ID cannot be null or blank for user handle generation.");
        }
        log.info("returning user handle");
        String userHandleString = "n" + user.getId().toString();
        log.info("userHandle: {}", new ByteArray(userHandleString.getBytes(StandardCharsets.UTF_8)));
        return new ByteArray(userHandleString.getBytes(StandardCharsets.UTF_8));
    }

    private String generateFlowId() {
        log.info("generateFlowId");
        byte[] randomBytes = new byte[32];
        random.nextBytes(randomBytes);
        return new ByteArray(randomBytes).getBase64Url();
    }


    // TODO REPLACE WITH PasskeyCredentialService file if possible
    // === CREDENTIAL REPOSITORY IMPLEMENTATION (INNER CLASS) ===
    private class JpaCredentialRepository implements CredentialRepository {

        public Set<CredentialRegistration> getRegistrationsByUsername(String email) throws UserNotFoundException {
            log.info("== getRegistrationsByUsername: {}", email);
            UsersEntity user = userRepository.findUserByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("user not found for passkey getRegistrationsByUsername"));
            if (user == null) return Set.of();

            Set<CredentialRegistration> set = new HashSet<>();
            log.info("== attempting to run for loop in findPasskeyByUserIdForUserRegistration(user.getId())");
            for (PasskeySummary passkeySummary : this.findPasskeyByUserIdForUserRegistration(user.getId())) {
                CredentialRegistration credentialRegistration = null;
                try {
                    credentialRegistration = toCredentialRegistration(passkeySummary);
                } catch (Base64UrlException e) {
                    // todo add custom exception
                    throw new RuntimeException(e);
                }
                set.add(credentialRegistration);
            }
            return set;
        }

        @Override
        public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
            log.info("== getCredentialIdsForUsername: {}", username);
            // Fetch all registrations for the username
            Set<CredentialRegistration> registrations = null;
            try {
                registrations = getRegistrationsByUsername(username);
            } catch (UserNotFoundException e) {
                throw new RuntimeException(e);
            }

            // Map each registration's credential to a PublicKeyCredentialDescriptor
            return registrations.stream()
                    .map(reg -> PublicKeyCredentialDescriptor.builder()
                            .id(reg.getCredential().getCredentialId())
                            .type(PublicKeyCredentialType.PUBLIC_KEY)
                            // TODO LOOK INTO UNDERSTANDING AND IMPLEMENTING TRANSPORTS
                            // Optionally include transports if you have them, e.g.:
                            // .transports(reg.getTransports())
                            .build())
                    .collect(Collectors.toSet());
        }

        @Override
        public Optional<ByteArray> getUserHandleForUsername(String email) {
            log.info("== getUserHandleForUsername {}", email);
            UsersEntity user = userRepository.findUserByEmail(email).orElseThrow(() -> new IllegalArgumentException("user does not exist"));
            return Optional.ofNullable(user).map(u -> {
                try {
                    log.info("generate user handle");
                    return generateUserHandle(u);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }); // Use the same generation logic
        }

        @Override
        public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
            log.info("== getUsernameForUserHandle");
            log.info("userHandle: {}", userHandle);
            log.info("userHandle bytes: {}", userHandle.getBytes());

            log.info("GET USER EMAIL FOR USERHANDLE");
            try {
                Optional<PasskeySummary> optionalPasskeySummary = this.findPasskeyByUserHandle(userHandle.getBytes());
                if (optionalPasskeySummary.isPresent()) {
                    log.info("passkey summary is present");
                    PasskeySummary passkeySummary = optionalPasskeySummary.get();
                    UsersEntity user = userRepository.findUserById(passkeySummary.getUserId());
                    return Optional.of(user.getEmail());
                } else {
                    log.info("passkey summary is not present");
                    return Optional.empty();
                }
            } catch (NumberFormatException e) {
                log.error("Could not parse user ID from userHandle: {}", userHandle.getBase64Url(), e);
                return Optional.empty();
            }
        }

        public Set<CredentialRegistration> getRegistrationsByUserHandle(ByteArray userHandle) {
            log.info("== getRegistrationsByUserHandle");
            // Find username from userHandle, then call getRegistrationsByUsername
            Set<CredentialRegistration> credentialRegistrations = getUsernameForUserHandle(userHandle)
                    .map(email -> {
                        try {
                            return getRegistrationsByUsername(email);
                        } catch (UserNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .orElse(Set.of());
            return credentialRegistrations;
        }

        // This method is used by Yubico library to look up a specific credential.
        // It's crucial for authentication.
        @Override
        public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
            log.info("== lookup");
            // The userHandle parameter here is the one provided by the authenticator during assertion.
            // It should match the userHandle stored with the credential.
            log.info("finding by credential id: {}", credentialId);
            log.info("finding by credential id base 64 url: {}", credentialId.getBase64Url());
            log.info("userHandle: {}", userHandle);

            log.info("=================================");
            log.info("PASSKEY SUMMARY");
            Optional<PasskeySummary> optionalPasskeySummary = this.findPasskeyByCredentialId(credentialId.getBytes());
            if (optionalPasskeySummary.isPresent()) {
                log.info("passkey summary: {}", optionalPasskeySummary.get());
                PasskeySummary passkeySummary = optionalPasskeySummary.get();
                log.info("Credential ID: {}", Base64.getUrlEncoder().encodeToString(passkeySummary.credentialId));

                RegisteredCredential registeredCredential = RegisteredCredential.builder()
                        .credentialId(new ByteArray(passkeySummary.credentialId))
                        .userHandle(new ByteArray(passkeySummary.userHandle))
                        .publicKeyCose(new ByteArray(passkeySummary.publicKeyCose))
                        .signatureCount(passkeySummary.signatureCount)
                        .build();
                log.info("REGISTERED CREDENTIAL: {}", registeredCredential);
                return Optional.of(registeredCredential);
            } else {
                throw new RuntimeException("optional passkey is not present");
            }
        }

        @Override
        public Set<RegisteredCredential> lookupAll(ByteArray userHandle) {
            log.info("== lookupAll");
            log.info("userHandle: {}", userHandle);
            return getRegistrationsByUserHandle(userHandle).stream()
                    .map(reg -> reg.getCredential()) // CredentialRegistration contains RegisteredCredential
                    .collect(Collectors.toSet());
        }

        private CredentialRegistration toCredentialRegistration(
                PasskeySummary passkeySummary
        ) throws Base64UrlException {
            log.info("== toCredentialRegistration PasskeySummary");
            log.info("passkey summary: {}", passkeySummary);

            UsersEntity user = userRepository.findUserById(passkeySummary.userId);

            // Ensure user handle used here is what the library expects (usually the one from UserIdentity)
            UserIdentity userIdentity = UserIdentity.builder()
                    .name(user.getEmail())
                    .displayName(user.getFirstName() + " " + user.getLastName())
                    .id(new ByteArray(passkeySummary.getUserHandle())) // This MUST match the userHandle stored on the entity
                    .build();

            java.util.Date regDate = passkeySummary.getRegisteredAt();

            return CredentialRegistration.builder()
                    .credential(RegisteredCredential.builder()
                            .credentialId(new ByteArray(passkeySummary.getCredentialId()))
                            .userHandle(userIdentity.getId()) // Use the same ID as in UserIdentity
                            .publicKeyCose(new ByteArray(passkeySummary.getPublicKeyCose()))
                            .signatureCount(passkeySummary.getSignatureCount())
                            .build())
                    .userIdentity(userIdentity)
                    .registrationTime(Instant.now())
                    // Optional: Add transports if your library version/config uses them here
                    // .transports(entity.getTransports().stream().map(AuthenticatorTransport::of).collect(Collectors.toSet()))
                    .build();
        }


        public List<PasskeySummary> findPasskeyByUserIdForUserRegistration(Long userId) {
            log.info("findPasskeyByUserIdForUserRegistration: {}", userId);
            String sql = "SELECT id, credential_id, public_key_cose, signature_count, user_handle, last_used_at, registered_at, user_id FROM ntail.passkey_credentials WHERE user_id = ?";

            List<PasskeySummary> results = jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) ->
                            new PasskeySummary(
                                    rs.getLong("id"),
                                    rs.getBytes("credential_id"),
                                    rs.getBytes("public_key_cose"),
                                    rs.getLong("signature_count"),
                                    rs.getBytes("user_handle"),
                                    rs.getDate("last_used_at"),
                                    rs.getDate("registered_at"),
                                    rs.getLong("user_id")
                            )
            );
            return results;
        }

        public Optional<PasskeySummary> findPasskeyByCredentialId(byte[] credentialId) {
            log.info("findPasskeyByCredentialId: {}", credentialId);
            String sql = "SELECT id, credential_id, public_key_cose, signature_count, user_handle, last_used_at, registered_at, user_id FROM ntail.passkey_credentials WHERE credential_id = ?";

            List<PasskeySummary> results = jdbcTemplate.query(sql, new Object[]{credentialId}, (rs, rowNum) ->
                    new PasskeySummary(
                            rs.getLong("id"),
                            rs.getBytes("credential_id"),
                            rs.getBytes("public_key_cose"),
                            rs.getLong("signature_count"),
                            rs.getBytes("user_handle"),
                            rs.getDate("last_used_at"),
                            rs.getDate("registered_at"),
                            rs.getLong("user_id")
                    )
            );

            return results.stream().findFirst();
        }

        public Optional<PasskeySummary> findPasskeyByUserHandle(byte[] userHandle) {
            log.info("findPasskeyByUserHandle: {}", userHandle);
            String sql = "SELECT id, credential_id, public_key_cose, signature_count, user_handle, last_used_at, registered_at, user_id FROM ntail.passkey_credentials WHERE user_handle = ?";

            List<PasskeySummary> results = jdbcTemplate.query(sql, new Object[]{userHandle}, (rs, rowNum) ->
                    new PasskeySummary(
                            rs.getLong("id"),
                            rs.getBytes("credential_id"),
                            rs.getBytes("public_key_cose"),
                            rs.getLong("signature_count"),
                            rs.getBytes("user_handle"),
                            rs.getDate("last_used_at"),
                            rs.getDate("registered_at"),
                            rs.getLong("user_id")
                    )
            );
            return results.stream().findFirst();
        }
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public class PasskeySummary {
        private Long id;
        private byte[] credentialId;
        private byte[] publicKeyCose;
        private long signatureCount;
        private byte[] userHandle;
        private Date lastUsedAt;
        private Date registeredAt;
        private Long userId;
    }

    public Optional<PasskeySummary> findPasskeyByCredentialId(byte[] credentialId) {
        log.info("findPasskeyByCredentialId 2: {}", credentialId);
        String sql = "SELECT id, credential_id, public_key_cose, signature_count, user_handle, last_used_at, registered_at, user_id FROM ntail.passkey_credentials WHERE credential_id = ?";

        List<PasskeySummary> results = jdbcTemplate.query(sql, new Object[]{credentialId}, (rs, rowNum) ->
                        new PasskeySummary(
                                rs.getLong("id"),
                                rs.getBytes("credential_id"),
                                rs.getBytes("public_key_cose"),
                                rs.getLong("signature_count"),
                                rs.getBytes("user_handle"),
                                rs.getDate("last_used_at"),
                                rs.getDate("registered_at"),
                                rs.getLong("user_id")
                        )
        );
        return results.stream().findFirst();
    }

    @Transactional
    public void savePasskeySummary(PasskeySummary ps) {
        log.info("savePasskeySummary");
        String sql = "UPDATE ntail.passkey_credentials SET " +
                "credential_id = ?, " +
                "last_used_at = ?, " +
                "public_key_cose = ?, " +
                "signature_count = ?, " +
                "user_handle = ? " +
                "WHERE id = ? RETURNING id";

        Long generatedId = jdbcTemplate.queryForObject(sql, Long.class,
                ps.getCredentialId(),
                ps.getLastUsedAt(),
                ps.getPublicKeyCose(),
                ps.getSignatureCount(),
                ps.getUserHandle(),
                ps.getId()
        );

        log.info("PASSKEY SUMMARY SAVED");
        ps.setId(generatedId);
    }
}



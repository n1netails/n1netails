package com.n1netails.n1netails.api.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException; // Added this import
import com.n1netails.n1netails.api.model.dto.passkey.PasskeyRegistrationFinishRequestDto;
import com.n1netails.n1netails.api.model.dto.passkey.PasskeyRegistrationStartRequestDto;
import com.n1netails.n1netails.api.model.dto.passkey.PasskeyRegistrationStartResponseDto;
import com.n1netails.n1netails.api.model.entity.PasskeyCredentialEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.repository.PasskeyCredentialRepository;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.yubico.webauthn.*;
import com.yubico.webauthn.data.CredentialRegistration; // Corrected import
import com.yubico.webauthn.data.*;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.dto.passkey.PasskeyAuthenticationFinishRequestDto;
import com.n1netails.n1netails.api.model.dto.passkey.PasskeyAuthenticationStartRequestDto;
import com.n1netails.n1netails.api.model.dto.passkey.PasskeyAuthenticationStartResponseDto;
import com.n1netails.n1netails.api.model.dto.passkey.PasskeyAuthenticationResponseDto;
import com.yubico.webauthn.exception.AssertionFailedException;
import com.yubico.webauthn.exception.RegistrationFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.n1netails.n1netails.api.constant.ProjectSecurityConstant.EXPIRATION_TIME;

@Service
@Slf4j
public class PasskeyService {

    private final UserRepository userRepository;
    private final PasskeyCredentialRepository passkeyCredentialRepository;
    private final RelyingParty relyingParty;
    private final Cache<String, PublicKeyCredentialCreationOptions> registrationCache;
    private final Cache<String, PublicKeyCredentialRequestOptions> authenticationCache;
    private final JwtEncoder jwtEncoder;
    private final AuthenticationManager authenticationManager; // To set authentication context

    private final SecureRandom random = new SecureRandom();

    @Autowired // Added Autowired for explicit dependency injection declaration
    public PasskeyService(UserRepository userRepository,
                          PasskeyCredentialRepository passkeyCredentialRepository,
                          @Value("${n1netails.passkey.relying-party-id}") String rpId,
                          @Value("${n1netails.passkey.relying-party-name}") String rpName,
                          @Value("${n1netails.passkey.origins}") Set<String> origins,
                          JwtEncoder jwtEncoder,
                          AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passkeyCredentialRepository = passkeyCredentialRepository;
        this.jwtEncoder = jwtEncoder;
        this.authenticationManager = authenticationManager;

        RelyingPartyIdentity rpIdentity = RelyingPartyIdentity.builder()
                .id(rpId)
                .name(rpName)
                .build();

        // Configure the RelyingParty instance
        this.relyingParty = RelyingParty.builder()
                .identity(rpIdentity)
                .credentialRepository(new JpaCredentialRepository())
                .origins(origins)
                .allowUntrustedAttestation(true) // Recommended for broader authenticator support, consider risks
                .allowOriginPort(true) // For localhost development
                .allowOriginSubdomain(true) // If you use subdomains
                .build();

        this.registrationCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
        this.authenticationCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .build();
    }

    // === REGISTRATION ===
    public PasskeyRegistrationStartResponseDto startRegistration(PasskeyRegistrationStartRequestDto request) throws UserNotFoundException {
        UsersEntity user = Optional.ofNullable(userRepository.findUserByUsername(request.getUsername()))
                .orElseThrow(() -> new UserNotFoundException("User not found: " + request.getUsername()));

        StartRegistrationOptions.Builder optionsBuilder = StartRegistrationOptions.builder()
                .user(UserIdentity.builder()
                        .name(user.getUsername())
                        .displayName(user.getFirstName() + " " + user.getLastName())
                        .id(generateUserHandle(user))
                        .build());

        // Exclude existing credentials for this user to prevent re-registration of the same key
        Set<PublicKeyCredentialDescriptor> excludeCredentials = passkeyCredentialRepository.findAllByUser(user).stream()
            .map(cred -> PublicKeyCredentialDescriptor.builder().id(ByteArray.fromBase64Url(cred.getCredentialId())).build())
            .collect(Collectors.toSet());
        if (!excludeCredentials.isEmpty()) {
            optionsBuilder.excludeCredentials(excludeCredentials);
        }

        optionsBuilder.authenticatorSelection(AuthenticatorSelectionCriteria.builder()
            .residentKey(ResidentKeyRequirement.PREFERRED) // Or .REQUIRED if you only want discoverable credentials
            .userVerification(UserVerificationRequirement.PREFERRED) // Or .REQUIRED for more security
            .build());

        PublicKeyCredentialCreationOptions credentialCreationOptions = relyingParty.startRegistration(optionsBuilder.build());
        String flowId = generateFlowId();
        registrationCache.put(flowId, credentialCreationOptions);

        log.info("Started passkey registration for user: {}, flowId: {}", user.getUsername(), flowId);
        return new PasskeyRegistrationStartResponseDto(flowId, credentialCreationOptions);
    }

    @Transactional
    public boolean finishRegistration(PasskeyRegistrationFinishRequestDto request) throws UserNotFoundException {
        PublicKeyCredentialCreationOptions creationOptions = registrationCache.getIfPresent(request.getFlowId());
        if (creationOptions == null) {
            log.warn("Passkey registration flow ID {} not found or expired.", request.getFlowId());
            return false; // Or throw specific exception
        }
        // Do NOT invalidate from cache immediately, finishRegistration might need it if it internally calls credentialRepository methods that check the cache

        try {
            RegistrationResult registrationResult = relyingParty.finishRegistration(FinishRegistrationOptions.builder()
                    .request(creationOptions)
                    .response(request.getCredential())
                    .build());

            // User should exist as it was checked in startRegistration
            UsersEntity user = Optional.ofNullable(userRepository.findUserByUsername(creationOptions.getUser().getName()))
                 .orElseThrow(() -> new UserNotFoundException("User " + creationOptions.getUser().getName() + " not found during finish registration."));


            PasskeyCredentialEntity passkeyCredential = new PasskeyCredentialEntity();
            passkeyCredential.setUser(user);
            passkeyCredential.setCredentialId(registrationResult.getKeyId().getCredentialId().getBase64Url());
            passkeyCredential.setPublicKeyCose(registrationResult.getPublicKeyCose().getBytes());
            passkeyCredential.setSignatureCount(registrationResult.getSignatureCount());
            passkeyCredential.setUserHandle(creationOptions.getUser().getId().getBase64Url()); // Store the user handle used during registration
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

            Set<String> transports = Optional.ofNullable(request.getCredential().getResponse().getTransports())
                                     .orElse(Set.of())
                                     .stream()
                                     .map(AuthenticatorTransport::getId)
                                     .collect(Collectors.toSet());
            passkeyCredential.setTransports(transports);

            Optional<AttestedCredentialData> attData = request.getCredential().getResponse().getAttestationObject().getAuthData().getAttestedCredentialData();
            attData.ifPresent(ad -> passkeyCredential.setAaguid(ad.getAaguid().toString()));


            passkeyCredentialRepository.save(passkeyCredential);
            registrationCache.invalidate(request.getFlowId()); // Invalidate after successful save
            log.info("Successfully registered passkey for user: {}, credentialId: {}", user.getUsername(), passkeyCredential.getCredentialId());
            return true;
        } catch (RegistrationFailedException e) {
            registrationCache.invalidate(request.getFlowId()); // Invalidate on failure too
            log.error("Passkey registration failed for user: {}. Reason: {}", creationOptions.getUser().getName(), e.getMessage(), e);
            return false; // Or throw specific exception
        } catch (ExecutionException e) { // This is for cache.get(), which we are not using here anymore with getIfPresent
             registrationCache.invalidate(request.getFlowId());
            log.error("Error during passkey registration for user: {}", creationOptions.getUser().getName(), e);
            return false;
        }
    }

    private String getAttestationType(PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> credential) {
        try {
            return credential.getResponse().getAttestationObject().getFormat();
        } catch (Exception e) {
            log.warn("Could not determine attestation format: {}", e.getMessage());
            return "unknown";
        }
    }


    // === AUTHENTICATION ===
    public PasskeyAuthenticationStartResponseDto startAuthentication(PasskeyAuthenticationStartRequestDto request) {
        StartAssertionOptions.Builder optionsBuilder = StartAssertionOptions.builder()
            .userVerification(UserVerificationRequirement.PREFERRED); // Or .REQUIRED

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            optionsBuilder.username(request.getUsername());
        }

        PublicKeyCredentialRequestOptions requestOptions = relyingParty.startAssertion(optionsBuilder.build());
        String flowId = generateFlowId();
        authenticationCache.put(flowId, requestOptions);

        log.info("Started passkey authentication, flowId: {}. For user (if provided): {}", flowId, request.getUsername());
        return new PasskeyAuthenticationStartResponseDto(flowId, requestOptions);
    }

    @Transactional
    public PasskeyAuthenticationResponseDto finishAuthentication(PasskeyAuthenticationFinishRequestDto request) {
        PublicKeyCredentialRequestOptions requestOptions = authenticationCache.getIfPresent(request.getFlowId());
        if (requestOptions == null) {
            log.warn("Passkey authentication flow ID {} not found or expired.", request.getFlowId());
            return new PasskeyAuthenticationResponseDto(false, "Authentication flow expired or invalid.");
        }
        // Do NOT invalidate from cache immediately for the same reasons as registration

        try {
            AssertionResult assertionResult = relyingParty.finishAssertion(FinishAssertionOptions.builder()
                    .request(requestOptions)
                    .response(request.getCredential())
                    .build());

            if (assertionResult.isSuccess()) {
                // IMPORTANT: Update the signature count in the database
                // The JpaCredentialRepository's updateSignatureCount method will be called by the library if configured correctly.
                // If not, or for belt-and-suspenders, ensure it's done:
                Optional<PasskeyCredentialEntity> credEntityOpt = passkeyCredentialRepository.findByCredentialId(assertionResult.getCredentialId().getBase64Url());
                if(credEntityOpt.isPresent()){
                    PasskeyCredentialEntity cred = credEntityOpt.get();
                    cred.setSignatureCount(assertionResult.getSignatureCount());
                    cred.setLastUsedAt(new Date());
                    passkeyCredentialRepository.save(cred);
                } else {
                     log.error("Passkey credential {} not found during finishAuthentication for signature count update.", assertionResult.getCredentialId().getBase64Url());
                     // This should not happen if authentication succeeded as the credential must exist.
                }


                UsersEntity user = userRepository.findUserByUsername(assertionResult.getUsername());
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
                userRepository.save(user);

                UserPrincipal userPrincipal = new UserPrincipal(user);

                // Manually create an Authentication object for the SecurityContext
                // This mimics what Spring Security would do for a password login
                // Note: For passkeys, authorities are usually derived from the user's existing roles, not the passkey itself.
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userPrincipal, null, userPrincipal.getAuthorities());
                // SecurityContextHolder.getContext().setAuthentication(authentication); // Set in Security Filter if needed, or if controller does it.
                // Here, we directly generate the token as the controller would.

                String jwtToken = createToken(userPrincipal);
                authenticationCache.invalidate(request.getFlowId());
                log.info("Successfully authenticated user {} with passkey credentialId: {}", user.getUsername(), assertionResult.getCredentialId().getBase64Url());
                return new PasskeyAuthenticationResponseDto(true, "Authentication successful", jwtToken, user);
            } else {
                authenticationCache.invalidate(request.getFlowId());
                log.warn("Passkey assertion failed for credentialId: {}. Username from assertion: {}", request.getCredential().getId().getBase64Url(), assertionResult.getUsername());
                return new PasskeyAuthenticationResponseDto(false, "Authentication failed.");
            }
        } catch (AssertionFailedException e) {
            authenticationCache.invalidate(request.getFlowId());
            log.error("Passkey assertion failed. Credential ID: {}. Message: {}", request.getCredential().getId().getBase64Url(), e.getMessage(), e);
            return new PasskeyAuthenticationResponseDto(false, "Authentication failed: " + e.getMessage());
        } catch (ExecutionException e) { // For cache
            authenticationCache.invalidate(request.getFlowId());
            log.error("Error during passkey authentication", e);
            return new PasskeyAuthenticationResponseDto(false, "An error occurred during authentication.");
        }
    }

    private String createToken(UserPrincipal userPrincipal) {
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("self") // TODO: Make configurable
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(EXPIRATION_TIME)) // EXPIRATION_TIME from constants
                .subject(userPrincipal.getUsername()) // email
                .claim("authorities", userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .claim("userId", ((UsersEntity)userPrincipal.getUser()).getUserId()) // Add userId to token
                .build();

        JwtEncoderParameters parameters = JwtEncoderParameters.from(claimsSet);
        return jwtEncoder.encode(parameters).getTokenValue();
    }


    // === UTILITY METHODS ===
    private ByteArray generateUserHandle(UsersEntity user) {
        // User handle MUST be stable and unique for the user.
        // It should NOT be PII if possible, and MUST NOT change for a given user.
        // Using user.getUserId() (which seems to be a string based on UsersEntity) is a good candidate if it's stable and unique.
        // The Yubico library expects a ByteArray. Max 64 bytes.
        // Ensure it's at least 1 byte, preferably more for uniqueness.
        String userId = user.getUserId(); // This is a String, e.g. "5107983742" based on generateUserId()
        if (userId == null || userId.isBlank()) {
            // This should not happen for an existing user
            log.error("User ID is null or blank for user: {}. Cannot generate user handle.", user.getUsername());
            throw new IllegalArgumentException("User ID cannot be null or blank for user handle generation.");
        }
        // Let's use the string representation of the user's primary ID (Long) as the user handle.
        // This is stable and unique.
        return ByteArray.fromBase64Url(user.getId().toString()); // Using DB ID (Long) for stability
    }

    private String generateFlowId() {
        byte[] randomBytes = new byte[32];
        random.nextBytes(randomBytes);
        return new ByteArray(randomBytes).getBase64Url();
    }


    // === CREDENTIAL REPOSITORY IMPLEMENTATION (INNER CLASS) ===
    private class JpaCredentialRepository implements CredentialRepository {

        @Override
        public Set<CredentialRegistration> getRegistrationsByUsername(String username) {
            UsersEntity user = userRepository.findUserByUsername(username);
            if (user == null) return Set.of();

            return passkeyCredentialRepository.findAllByUser(user).stream()
                .map(this::toCredentialRegistration)
                .collect(Collectors.toSet());
        }

        @Override
        public Optional<CredentialRegistration> getRegistrationByUsernameAndCredentialId(String username, ByteArray credentialId) {
            UsersEntity user = userRepository.findUserByUsername(username);
            if (user == null) {
                return Optional.empty();
            }
            return passkeyCredentialRepository.findByUserAndCredentialId(user, credentialId.getBase64Url())
                    .map(this::toCredentialRegistration);
        }


        @Override
        public Optional<ByteArray> getUserHandleForUsername(String username) {
            UsersEntity user = userRepository.findUserByUsername(username);
            return Optional.ofNullable(user).map(u -> generateUserHandle(u)); // Use the same generation logic
        }

        @Override
        public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
            // This is the tricky part: mapping userHandle back to username.
            // Since we're using user.getId().toString() as base64url for userHandle:
            try {
                Long userId = Long.parseLong(userHandle.getBase64Url()); // Assuming userHandle is base64url of the Long ID.
                                                                      // Actually, if it's fromBase64Url(id.toString()), then it is id.toString().
                                                                      // So, it should be new ByteArray(id.toString().getBytes(StandardCharsets.UTF_8)).getBase64Url()
                                                                      // and to reverse: new String(userHandle.getBytes(), StandardCharsets.UTF_8)
                                                                      // For simplicity, let's assume userHandle in DB is stored as the user.getId().toString()

                // Correct approach: The userHandle stored in PasskeyCredentialEntity should be used for lookup.
                // The userHandle in PasskeyCredentialEntity is `creationOptions.getUser().getId().getBase64Url()`
                // which is `generateUserHandle(user).getBase64Url()` which is `ByteArray.fromBase64Url(user.getId().toString()).getBase64Url()`
                // This means the stored userHandle is effectively user.getId().toString().
                String userIdStr = userHandle.getBase64Url(); // This is user.getId().toString()

                return passkeyCredentialRepository.findAll().stream() // Inefficient: Iterate all credentials
                    .filter(cred -> cred.getUserHandle() != null && cred.getUserHandle().equals(userIdStr))
                    .map(cred -> cred.getUser().getUsername())
                    .findFirst();

            } catch (NumberFormatException e) {
                log.error("Could not parse user ID from userHandle: {}", userHandle.getBase64Url(), e);
                return Optional.empty();
            }
        }


        @Override
        public Set<CredentialRegistration> getRegistrationsByUserHandle(ByteArray userHandle) {
            // Find username from userHandle, then call getRegistrationsByUsername
            return getUsernameForUserHandle(userHandle)
                .map(this::getRegistrationsByUsername)
                .orElse(Set.of());
        }

        // This method is used by Yubico library to look up a specific credential.
        // It's crucial for authentication.
        @Override
        public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
            // The userHandle parameter here is the one provided by the authenticator during assertion.
            // It should match the userHandle stored with the credential.
            return passkeyCredentialRepository.findByCredentialId(credentialId.getBase64Url())
                .filter(cred -> cred.getUserHandle() != null && cred.getUserHandle().equals(userHandle.getBase64Url()))
                .map(credEntity -> RegisteredCredential.builder()
                        .credentialId(ByteArray.fromBase64Url(credEntity.getCredentialId()))
                        .userHandle(ByteArray.fromBase64Url(credEntity.getUserHandle()))
                        .publicKeyCose(new ByteArray(credEntity.getPublicKeyCose()))
                        .signatureCount(credEntity.getSignatureCount())
                        .build());
        }

        // This method is called by the library to persist a new credential.
        // Since we save manually in finishRegistration, this might not be strictly needed
        // if all library paths lead to our manual save. However, it's good practice to implement.
        @Override
        public boolean addRegistration(RegisteredCredential registration) {
            log.warn("JpaCredentialRepository.addRegistration called. This indicates an unexpected flow or misconfiguration, as credentials should be saved via PasskeyService.finishRegistration.");
            // To prevent potential issues, avoid saving here if finishRegistration is the primary path.
            // If this method IS indeed part of a flow you intend to use, you'd implement the logic
            // to convert RegisteredCredential to PasskeyCredentialEntity and save it.
            // This would involve looking up the user by userHandle.
            return false; // Indicate it wasn't (or shouldn't be) handled here.
        }

        @Override
        public boolean removeRegistration(ByteArray userHandle, ByteArray credentialId) {
            // Implementation for deleting a credential
             Optional<String> usernameOpt = getUsernameForUserHandle(userHandle);
            if (usernameOpt.isEmpty()) {
                log.warn("Cannot remove registration: User not found for userHandle {}", userHandle.getBase64Url());
                return false;
            }
            UsersEntity user = userRepository.findUserByUsername(usernameOpt.get());
            if (user == null) { // Should not happen if usernameOpt was present
                log.warn("Cannot remove registration: User {} (from handle {}) not found in DB", usernameOpt.get(), userHandle.getBase64Url());
                return false;
            }

            Optional<PasskeyCredentialEntity> cred = passkeyCredentialRepository.findByUserAndCredentialId(user, credentialId.getBase64Url());
            if (cred.isPresent()) {
                passkeyCredentialRepository.delete(cred.get());
                log.info("Removed passkey credential {} for user {}", credentialId.getBase64Url(), user.getUsername());
                return true;
            } else {
                log.warn("Passkey credential {} not found for user {} during removal attempt.", credentialId.getBase64Url(), user.getUsername());
                return false;
            }
        }


        // This method is crucial and is called by the library after a successful assertion.
        @Override
        public boolean updateSignatureCount(AssertionResult assertionResult) {
            Optional<PasskeyCredentialEntity> credEntityOpt = passkeyCredentialRepository.findByCredentialId(assertionResult.getCredentialId().getBase64Url());
            if (credEntityOpt.isPresent()) {
                PasskeyCredentialEntity credEntity = credEntityOpt.get();
                if (credEntity.getUserHandle().equals(assertionResult.getUserHandle().getBase64Url())) { // Ensure it's the correct user's credential
                    credEntity.setSignatureCount(assertionResult.getSignatureCount());
                    credEntity.setLastUsedAt(new Date()); // Also update last used timestamp
                    passkeyCredentialRepository.save(credEntity);
                    log.debug("Updated signature count for credential {} to {}", assertionResult.getCredentialId().getBase64Url(), assertionResult.getSignatureCount());
                    return true;
                } else {
                     log.warn("Attempted to update signature count for credential {} but user handle did not match. DB: {}, Assertion: {}", assertionResult.getCredentialId().getBase64Url(), credEntity.getUserHandle(), assertionResult.getUserHandle().getBase64Url());
                     return false;
                }
            } else {
                log.warn("Could not find credential {} to update signature count.", assertionResult.getCredentialId().getBase64Url());
                return false;
            }
        }

        private CredentialRegistration toCredentialRegistration(PasskeyCredentialEntity entity) {
            // Ensure user handle used here is what the library expects (usually the one from UserIdentity)
            UserIdentity userIdentity = UserIdentity.builder()
                .name(entity.getUser().getUsername())
                .displayName(entity.getUser().getFirstName() + " " + entity.getUser().getLastName())
                .id(ByteArray.fromBase64Url(entity.getUserHandle())) // This MUST match the userHandle stored on the entity
                .build();

            return CredentialRegistration.builder()
                .credential(RegisteredCredential.builder()
                    .credentialId(ByteArray.fromBase64Url(entity.getCredentialId()))
                    .userHandle(userIdentity.getId()) // Use the same ID as in UserIdentity
                    .publicKeyCose(new ByteArray(entity.getPublicKeyCose()))
                    .signatureCount(entity.getSignatureCount())
                    .build())
                .userIdentity(userIdentity)
                .registrationTime(entity.getRegisteredAt() != null ? entity.getRegisteredAt().toInstant() : Instant.now())
                // Optional: Add transports if your library version/config uses them here
                // .transports(entity.getTransports().stream().map(AuthenticatorTransport::of).collect(Collectors.toSet()))
                .build();
        }
    }
}

package com.n1netails.n1netails.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.exception.type.UsernameExistsException;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.PasskeyCredentialEntity;
import com.n1netails.n1netails.api.model.entity.PasskeyRegistrationRequestEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.passkey.PasskeyLoginFinishRequest;
import com.n1netails.n1netails.api.model.request.passkey.PasskeyLoginStartRequest;
import com.n1netails.n1netails.api.model.request.passkey.PasskeyRegistrationFinishRequest;
import com.n1netails.n1netails.api.model.request.passkey.PasskeyRegistrationStartRequest;
import com.n1netails.n1netails.api.model.response.passkey.PasskeyAuthenticationResponse;
import com.n1netails.n1netails.api.model.response.passkey.PasskeyLoginStartResponse;
import com.n1netails.n1netails.api.model.response.passkey.PasskeyRegistrationStartResponse;
import com.n1netails.n1netails.api.repository.PasskeyCredentialRepository;
import com.n1netails.n1netails.api.repository.PasskeyRegistrationRequestRepository;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.service.PasskeyService;
import com.yubico.webauthn.*;
import com.yubico.webauthn.data.*;
import com.yubico.webauthn.exception.AssertionFailedException;
import com.yubico.webauthn.exception.RegistrationFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.GrantedAuthority;


import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.n1netails.n1netails.api.constant.ProjectSecurityConstant.EXPIRATION_TIME;


@Slf4j
@Service
@RequiredArgsConstructor
public class PasskeyServiceImpl implements PasskeyService {

    private final RelyingParty relyingParty;
    private final UserRepository userRepository;
    private final PasskeyCredentialRepository passkeyCredentialRepository;
    private final PasskeyRegistrationRequestRepository registrationRequestRepository;
    private final ObjectMapper objectMapper; // For converting Yubico objects to JSON strings and vice-versa
    private final JwtEncoder jwtEncoder;

    private static final long REGISTRATION_REQUEST_EXPIRY_MINUTES = 5;


    @Override
    @Transactional
    public PasskeyRegistrationStartResponse startRegistration(PasskeyRegistrationStartRequest request) {
        log.info("Starting passkey registration for username: {}", request.getUsername());
        UsersEntity user = userRepository.findUserByEmail(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getUsername()));

        // Check if user already has passkeys and if we want to limit them or just add another
        // For now, allow multiple passkeys per user.

        // Generate a user handle for WebAuthn - this should be a stable, non-personally identifiable ID for the user.
        // We can use the user's database ID or a generated UUID associated with the user.
        // For Yubico's library, this user handle is what's stored in UserIdentity.id
        // Let's use a base64url encoded version of user's primary ID for now.
        // Important: This handle should be consistent for the user across all their credentials.
        ByteArray userHandle = ByteArray.fromBase64Url(user.getUserId()); // Assuming UsersEntity.getUserId() returns a suitable string ID.

        UserIdentity userIdentity = UserIdentity.builder()
                .name(user.getEmail()) // Typically the username they log in with
                .displayName(request.getDisplayName() != null ? request.getDisplayName() : user.getFirstName() + " " + user.getLastName())
                .id(userHandle) // This is the UserHandle
                .build();

        StartRegistrationOptions registrationOptions = StartRegistrationOptions.builder()
                .user(userIdentity)
                .authenticatorSelection(AuthenticatorSelectionCriteria.builder()
                        .residentKey(ResidentKeyRequirement.PREFERRED) // Prefer discoverable credentials
                        .userVerification(UserVerificationRequirement.PREFERRED)
                        .build())
                .build();

        PublicKeyCredentialCreationOptions creationOptions = relyingParty.startRegistration(registrationOptions);

        try {
            String optionsJson = creationOptions.toJson();
            String requestId = creationOptions.getChallenge().getBase64Url(); // Use challenge as request ID

            PasskeyRegistrationRequestEntity requestEntity = PasskeyRegistrationRequestEntity.builder()
                    .requestId(requestId)
                    .userId(user.getId())
                    .username(user.getEmail())
                    .registrationOptions(optionsJson)
                    .build();
            registrationRequestRepository.save(requestEntity);

            log.info("Passkey registration started for user: {}, challenge: {}", user.getEmail(), requestId);
            return new PasskeyRegistrationStartResponse(requestId, optionsJson);

        } catch (JsonProcessingException e) {
            log.error("Error serializing PublicKeyCredentialCreationOptions to JSON", e);
            throw new RuntimeException("Failed to start passkey registration due to JSON processing error.", e);
        }
    }

    @Override
    @Transactional
    public void finishRegistration(PasskeyRegistrationFinishRequest request) {
        log.info("Finishing passkey registration for request ID: {}", request.getRegistrationId());

        PasskeyRegistrationRequestEntity requestEntity = registrationRequestRepository.findByRequestId(request.getRegistrationId())
                .orElseThrow(() -> new RuntimeException("Passkey registration request not found or expired: " + request.getRegistrationId()));

        // Clean up expired requests (optional, can be a scheduled task too)
        registrationRequestRepository.deleteAllByCreatedAtBefore(OffsetDateTime.now().minusMinutes(REGISTRATION_REQUEST_EXPIRY_MINUTES + 5));


        try {
            PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> pkc =
                    PublicKeyCredential.parseRegistrationResponseJson(request.getCredential());

            PublicKeyCredentialCreationOptions creationOptions = PublicKeyCredentialCreationOptions.fromJson(requestEntity.getRegistrationOptions());

            RegistrationResult registrationResult = relyingParty.finishRegistration(FinishRegistrationOptions.builder()
                    .request(creationOptions)
                    .response(pkc)
                    .build());

            UsersEntity user = userRepository.findById(requestEntity.getUserId()) // Assuming userId was stored
                    .orElseThrow(() -> new UserNotFoundException("User not found for registration request."));


            // Check for existing friendly name for this user
            String friendlyName = pkc.getClientExtensionResults().getCredProps() != null && pkc.getClientExtensionResults().getCredProps().isRk() ? "Discoverable Passkey" : "Passkey";
            // Potentially allow user to name it or generate a default name
            int count = 1;
            String baseName = friendlyName;
            while(passkeyCredentialRepository.existsByUserAndFriendlyName(user, friendlyName)) {
                friendlyName = baseName + " " + (++count);
            }


            PasskeyCredentialEntity credentialEntity = PasskeyCredentialEntity.builder()
                    .user(user)
                    .externalId(registrationResult.getKeyId().getId().getBase64Url())
                    .publicKeyCose(registrationResult.getPublicKeyCose().getBase64Url())
                    .count(registrationResult.getSignatureCount())
                    .aaguid(pkc.getResponse().getAttestation().getAuthenticatorData().getAaguid().getBase64Url()) // Store AAGUID
                    .userHandle(creationOptions.getUser().getId().getBase64Url()) // Store the user handle used
                    .credentialType("public-key") // Standard type
                    .friendlyName(friendlyName) // Set a default friendly name
                    .userAgent(null) // User-Agent should be captured from HTTP request if needed
                    .build();
            passkeyCredentialRepository.save(credentialEntity);
            registrationRequestRepository.delete(requestEntity); // Clean up successful request

            log.info("Passkey registration finished successfully for user: {}, credential ID: {}", user.getEmail(), credentialEntity.getExternalId());

        } catch (JsonProcessingException e) {
            log.error("Error processing JSON for passkey registration finish: {}", request.getRegistrationId(), e);
            registrationRequestRepository.delete(requestEntity); // Clean up failed request
            throw new RuntimeException("Failed to finish passkey registration due to JSON error.", e);
        } catch (RegistrationFailedException e) {
            log.warn("Passkey registration failed for request ID: {}. Reason: {}", request.getRegistrationId(), e.getMessage(), e);
            registrationRequestRepository.delete(requestEntity); // Clean up failed request
            throw new RuntimeException("Passkey registration failed: " + e.getMessage(), e);
        }
    }


    @Override
    @Transactional(readOnly = true) // Typically start of login is read-only
    public PasskeyLoginStartResponse startLogin(PasskeyLoginStartRequest request) {
        log.info("Starting passkey login for username: {}", request != null ? request.getUsername() : "N/A (discoverable)");

        StartAssertionOptions.Builder optionsBuilder = StartAssertionOptions.builder();
        if (request != null && request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            optionsBuilder.username(request.getUsername());
        }
        // For discoverable credentials, username might not be provided initially.
        // The RelyingParty library handles this.

        PublicKeyCredentialRequestOptions requestOptions = relyingParty.startAssertion(optionsBuilder.build());

        try {
            String optionsJson = requestOptions.toJson();
            String assertionId = requestOptions.getChallenge().getBase64Url(); // Use challenge as assertion ID

            // We don't strictly need to store the assertion request for the Yubico library's server-side,
            // as finishAssertion takes the challenge directly.
            // However, if we wanted to link it to a user session or add expiry, we could store it.
            // For simplicity here, we'll just return it. The client needs to send back the challenge (assertionId).

            log.info("Passkey login started, challenge: {}", assertionId);
            return new PasskeyLoginStartResponse(assertionId, optionsJson);

        } catch (JsonProcessingException e) {
            log.error("Error serializing PublicKeyCredentialRequestOptions to JSON", e);
            throw new RuntimeException("Failed to start passkey login due to JSON processing error.", e);
        }
    }

    @Override
    @Transactional
    public PasskeyAuthenticationResponse finishLogin(PasskeyLoginFinishRequest request) {
        log.info("Finishing passkey login for assertion ID (challenge): {}", request.getAssertionId());

        try {
            PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> pkc =
                    PublicKeyCredential.parseAssertionResponseJson(request.getCredential());

            FinishAssertionOptions finishOptions = FinishAssertionOptions.builder()
                    .request(AssertionRequest.builder() // Build AssertionRequest from the challenge received
                        .challenge(ByteArray.fromBase64Url(request.getAssertionId()))
                        // .publicKeyCredentialRequestOptions( ... ) // If we stored the full options earlier, we could pass them.
                                                                // Yubico lib can often work with just challenge + response.
                        .build())
                    .response(pkc)
                    .build();

            AssertionResult assertionResult = relyingParty.finishAssertion(finishOptions);

            if (assertionResult.isSuccess()) {
                PasskeyCredentialEntity credentialEntity = passkeyCredentialRepository
                        .findByExternalId(pkc.getId().getBase64Url()) // pkc.getId() is the credential ID
                        .stream().findFirst() // Assuming credential ID is globally unique
                        .orElseThrow(() -> new AssertionFailedException("Authenticated passkey not found in repository."));

                // Update signature count and last used time
                credentialEntity.setCount(assertionResult.getSignatureCount());
                credentialEntity.setLastUsedAt(OffsetDateTime.now());
                passkeyCredentialRepository.save(credentialEntity);

                UsersEntity user = credentialEntity.getUser();
                UserPrincipal userPrincipal = new UserPrincipal(user); // Create UserPrincipal for token generation

                String token = createToken(userPrincipal);

                log.info("Passkey login successful for user: {}, credential ID: {}", user.getEmail(), credentialEntity.getExternalId());
                return new PasskeyAuthenticationResponse(token, user.getEmail(), "Passkey login successful.");
            } else {
                log.warn("Passkey assertion failed for assertion ID: {}", request.getAssertionId());
                throw new AssertionFailedException("Passkey assertion failed.");
            }

        } catch (JsonProcessingException e) {
            log.error("Error processing JSON for passkey login finish: {}", request.getAssertionId(), e);
            throw new RuntimeException("Failed to finish passkey login due to JSON error.", e);
        } catch (AssertionFailedException e) {
            log.warn("Passkey login failed for assertion ID: {}. Reason: {}", request.getAssertionId(), e.getMessage(), e);
            throw new RuntimeException("Passkey login failed: " + e.getMessage(), e);
        }
    }

    // Based on UserController's createToken method
    private String createToken(UserPrincipal userPrincipal) {
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("self") // TODO: Make this configurable
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(EXPIRATION_TIME)) // EXPIRATION_TIME from ProjectSecurityConstant
                .subject(userPrincipal.getUsername()) // This is the email
                .claim("authorities", userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .claim("userId", ((UsersEntity)userPrincipal.getUser()).getUserId()) // Add user ID if needed by frontend
                .claim("firstName", ((UsersEntity)userPrincipal.getUser()).getFirstName())
                .claim("lastName", ((UsersEntity)userPrincipal.getUser()).getLastName())
                .build();

        JwtEncoderParameters parameters = JwtEncoderParameters.from(claimsSet);
        return jwtEncoder.encode(parameters).getTokenValue();
    }

    // Method to clean up stale registration requests (can be called by a scheduler)
    @Transactional
    public void cleanupStaleRegistrationRequests() {
        OffsetDateTime expiryTime = OffsetDateTime.now().minusMinutes(REGISTRATION_REQUEST_EXPIRY_MINUTES);
        log.info("Cleaning up passkey registration requests older than {}", expiryTime);
        registrationRequestRepository.deleteAllByCreatedAtBefore(expiryTime); // Returns void
        log.info("Attempted deletion of stale passkey registration requests older than {}.", expiryTime);
    }
}

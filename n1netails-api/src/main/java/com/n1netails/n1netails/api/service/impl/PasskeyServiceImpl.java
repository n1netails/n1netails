package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.model.entity.UserAuthenticator;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.repository.UserAuthenticatorRepository;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.service.PasskeyService;
import com.n1netails.n1netails.api.webauthn.N1netailsCredentialRepository;
import com.yubico.webauthn.*;
import com.yubico.webauthn.data.*;
import com.yubico.webauthn.exception.AssertionFailedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yubico.webauthn.exception.RegistrationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PasskeyServiceImpl implements PasskeyService {

    private static final Logger logger = LoggerFactory.getLogger(PasskeyServiceImpl.class);

    private final N1netailsCredentialRepository credentialRepository;
    private final UserRepository userRepository;
    private final UserAuthenticatorRepository userAuthenticatorRepository;
    private final RelyingParty relyingParty;
    private final ObjectMapper objectMapper = new ObjectMapper(); // For deserializing the challenge options

    @Autowired
    public PasskeyServiceImpl(N1netailsCredentialRepository credentialRepository,
                              UserRepository userRepository,
                              UserAuthenticatorRepository userAuthenticatorRepository,
                              @Value("${n1netails.webauthn.relying-party-id}") String rpId,
                              @Value("${n1netails.webauthn.relying-party-name}") String rpName,
                              @Value("${n1netails.webauthn.allowed-origins}") String allowedOriginsString) {
        this.credentialRepository = credentialRepository;
        this.userRepository = userRepository;
        this.userAuthenticatorRepository = userAuthenticatorRepository;

        RelyingPartyIdentity rpIdentity = RelyingPartyIdentity.builder()
                .id(rpId)
                .name(rpName)
                .build();

        Set<String> allowedOrigins = Set.of(allowedOriginsString.split(",\\s*"));

        this.relyingParty = RelyingParty.builder()
                .identity(rpIdentity)
                .credentialRepository(this.credentialRepository)
                .origins(allowedOrigins)
                // .attestationConveyancePreference(AttestationConveyancePreference.DIRECT) // Optional
                .build();
    }


    @Override
    @Transactional
    public PublicKeyCredentialCreationOptions startRegistration(
            String email, // Changed from username to email
            String relyingPartyIdDomain,
            String relyingPartyName,
            String clientOrigin) {

        // Find user by email
        UsersEntity user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email not found for registration start: " + email));

        UserIdentity userIdentity = UserIdentity.builder()
                .name(user.getUsername()) // Still use username for UserIdentity.name if that's the convention
                .displayName(user.getFirstName() + " " + user.getLastName())
                .id(user.getWebAuthnUserHandle()) // This is crucial, uses the user's stable handle
                .build();

        // Exclude credentials based on the actual username or user handle derived from the found user
        Set<PublicKeyCredentialDescriptor> excludeCredentials =
            credentialRepository.getCredentialIdsForUsername(user.getUsername()).stream()
                .map(descriptor -> PublicKeyCredentialDescriptor.builder().id(descriptor.getId()).build())
                .collect(Collectors.toSet());

        StartRegistrationOptions options = StartRegistrationOptions.builder()
                .user(userIdentity)
                .authenticatorSelection(AuthenticatorSelectionCriteria.builder()
                    .residentKey(ResidentKeyRequirement.PREFERRED)
                    .userVerification(UserVerificationRequirement.PREFERRED)
                    .build())
                .excludeCredentials(excludeCredentials)
                .build();
        // The PublicKeyCredentialCreationOptions returned here contains the challenge.
        // This options object (or at least its JSON representation) needs to be temporarily stored
        // by the caller (e.g., in HTTP session or a short-lived cache) and passed to finishRegistration.
        return this.relyingParty.startRegistration(options);
    }

    @Override
    @Transactional
    public boolean finishRegistration(
            String email, // Changed from username to email
            String relyingPartyIdDomain,
            String clientOrigin,
            AuthenticatorAttestationResponse attestationResponse,
            ClientRegistrationExtensionOutputs clientExtensions,
            String originalCreationOptionsJson) {

        // Find user by email
        UsersEntity user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email not found for registration finish: " + email));

        PublicKeyCredentialCreationOptions originalCreationOptions;
        try {
            originalCreationOptions = objectMapper.readValue(originalCreationOptionsJson, PublicKeyCredentialCreationOptions.class);
        } catch (JsonProcessingException e) {
            logger.error("Failed to deserialize PublicKeyCredentialCreationOptions from JSON", e);
            throw new IllegalArgumentException("Invalid originalCreationOptionsJson provided.", e);
        }

        PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> pkc =
                PublicKeyCredential.<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs>builder()
                        .id(attestationResponse.getKeyId().getId())
                        .response(attestationResponse)
                        .clientExtensionResults(clientExtensions != null ? clientExtensions : ClientRegistrationExtensionOutputs.empty())
                        .type(PublicKeyCredentialType.PUBLIC_KEY)
                        .build();

        FinishRegistrationOptions options = FinishRegistrationOptions.builder()
                .request(RegistrationRequest.builder()
                        .publicKeyCredentialCreationOptions(originalCreationOptions) // Use the restored options
                        .build())
                .response(pkc)
                // .rpId(Optional.of(relyingPartyIdDomain)) // Only if RP ID is not fixed
                .build();

        try {
            RegistrationResult registrationResult = this.relyingParty.finishRegistration(options);

            UserAuthenticator authenticator = UserAuthenticator.builder()
                    .user(user)
                    .name("Passkey - " + Instant.now().toString().substring(0, 10))
                    .rpId(this.relyingParty.getIdentity().getId())
                    .credentialId(registrationResult.getKeyId().getId())
                    .publicKey(registrationResult.getPublicKeyCose())
                    .attestationType(registrationResult.getAttestationType())
                    .aaguid(registrationResult.getAaguid().orElse(null))
                    .signCount(registrationResult.getSignatureCount())
                    .transportsEnum(registrationResult.getTransports().orElse(Collections.emptyList()))
                    .backupEligible(registrationResult.isBackupEligible())
                    .backupState(registrationResult.isBackedUp())
                    .userHandle(user.getWebAuthnUserHandle())
                    .createdAt(Instant.now())
                    .build();

            credentialRepository.saveAuthenticator(authenticator);
            logger.info("Passkey registration successful for user: {}, credential ID: {}", user.getUsername(), registrationResult.getKeyId().getId().getBase64Url());
            return true;
        } catch (RegistrationFailedException e) {
            logger.error("Passkey registration failed for user: {}: {}", user.getUsername(), e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error during passkey registration for user: {}: {}", user.getUsername(), e.getMessage(), e);
            return false;
        }
    }


    @Override
    public PublicKeyCredentialRequestOptions startAuthentication(String email, String relyingPartyIdDomain) {
        // email is optional. If provided, it can be used to fetch the user's username
        // for UserIdentity, which might help some authenticators.
        // For discoverable credentials, email (and thus username) can be null.
        Optional<String> usernameForAssertion = Optional.empty();
        if (email != null && !email.isEmpty()) {
            Optional<UsersEntity> userOptional = userRepository.findUserByEmail(email);
            if (userOptional.isPresent()) {
                usernameForAssertion = Optional.of(userOptional.get().getUsername());
            } else {
                // User not found by email, proceed with discoverable login (username will be empty)
                logger.warn("User not found by email {} for startAuthentication. Proceeding with discoverable login.", email);
            }
        }

        StartAssertionOptions options = StartAssertionOptions.builder()
                .username(usernameForAssertion) // Use username if found, otherwise empty for discoverable
                .userVerification(Optional.of(UserVerificationRequirement.PREFERRED))
                .build();
        return this.relyingParty.startAssertion(options);
    }

    @Override
    @Transactional
    public Optional<UserIdentity> finishAuthentication(
            String relyingPartyIdDomain, // Can be ignored
            String clientOrigin,         // Can be ignored
            AuthenticatorAssertionResponse assertionResponse,
            String originalRequestOptionsJson) { // Expecting the JSON of PublicKeyCredentialRequestOptions

        PublicKeyCredentialRequestOptions originalRequestOptions;
        try {
            originalRequestOptions = objectMapper.readValue(originalRequestOptionsJson, PublicKeyCredentialRequestOptions.class);
        } catch (JsonProcessingException e) {
            logger.error("Failed to deserialize PublicKeyCredentialRequestOptions from JSON", e);
            throw new IllegalArgumentException("Invalid originalRequestOptionsJson provided.", e);
        }

        PublicKeyCredential<AuthenticatorAssertionResponse, ClientAuthenticationExtensionOutputs> pkc =
                PublicKeyCredential.<AuthenticatorAssertionResponse, ClientAuthenticationExtensionOutputs>builder()
                        .id(assertionResponse.getKeyId().getId())
                        .response(assertionResponse)
                        .clientExtensionResults(ClientAuthenticationExtensionOutputs.empty())
                        .type(PublicKeyCredentialType.PUBLIC_KEY)
                        .build();

        FinishAssertionOptions options = FinishAssertionOptions.builder()
                .request(AssertionRequest.builder()
                        .publicKeyCredentialRequestOptions(originalRequestOptions) // Use the restored options
                        // .username(Optional.ofNullable(assertionResult.getUsername())) // If username was part of the original request options
                        .build())
                .response(pkc)
                .build();

        try {
            AssertionResult assertionResult = this.relyingParty.finishAssertion(options);

            if (assertionResult.isSuccess()) {
                UserAuthenticator authenticator = credentialRepository.findByCredentialId(assertionResult.getCredential().getCredentialId())
                    .orElseThrow(() -> new AssertionFailedException("Authenticated credential not found in repository: " + assertionResult.getCredential().getCredentialId()));

                authenticator.setSignCount(assertionResult.getSignatureCount());
                authenticator.setLastUsedAt(Instant.now());
                credentialRepository.updateAuthenticator(authenticator);

                logger.info("Passkey authentication successful for user: {}, credential ID: {}", assertionResult.getUsername(), assertionResult.getCredential().getCredentialId().getBase64Url());
                return Optional.of(UserIdentity.builder()
                        .name(assertionResult.getUsername())
                        .displayName(assertionResult.getUsername()) // Fetch full name if needed
                        .id(assertionResult.getUserHandle())
                        .build());
            } else {
                logger.warn("Passkey authentication failed for user: {}", assertionResult.getUsername()); // Username might not be present if assertion failed early
                return Optional.empty();
            }
        } catch (AssertionFailedException e) {
            logger.error("Passkey assertion failed: {}", e.getMessage(), e);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Unexpected error during passkey assertion: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
    // Removed toUserIdentity as it's no longer directly used with a generic User model input.
    // UserIdentity is constructed directly from UsersEntity.
}

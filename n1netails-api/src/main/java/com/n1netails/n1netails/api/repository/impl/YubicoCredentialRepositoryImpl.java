package com.n1netails.n1netails.api.repository.impl;

import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.dto.PasskeySummary;
import com.n1netails.n1netails.api.model.dto.passkey.CredentialRegistration;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.repository.PasskeyCredentialRepository;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.data.PublicKeyCredentialType;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.data.exception.Base64UrlException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.n1netails.n1netails.api.util.PasskeyUtil.generateUserHandle;

@Slf4j
@RequiredArgsConstructor
@Repository
public class YubicoCredentialRepositoryImpl implements CredentialRepository {

    private final PasskeyCredentialRepository passkeyCredentialRepository;
    private final UserRepository userRepository;

    public Set<CredentialRegistration> getRegistrationsByUsername(String emailAsUsername) throws UserNotFoundException {
        log.info("== getRegistrationsByUsername for a provided username hint (which is an email).");
        UsersEntity user = userRepository.findUserByEmail(emailAsUsername)
                .orElseThrow(() -> {
                    log.warn("User not found using the provided email hint during getRegistrationsByUsername.");
                    return new UserNotFoundException("User not found for passkey getRegistrationsByUsername using provided email hint.");
                });

        Set<CredentialRegistration> set = new HashSet<>();
        log.debug("Attempting to find passkeys by userId: {} (username: {})", user.getId(), user.getUsername());
        for (PasskeySummary passkeySummary : passkeyCredentialRepository.findPasskeyByUserIdForUserRegistration(user.getId())) {
            CredentialRegistration credentialRegistration = null;
            try {
                credentialRegistration = toCredentialRegistration(passkeySummary);
            } catch (Base64UrlException e) {
                log.error("Error occurred Generating Credential Registration for user {}: {}", user.getUsername(), e.getMessage(), e);
                throw new RuntimeException(e);
            }
            set.add(credentialRegistration);
        }
        log.info("Found {} registrations for user: {}", set.size(), user.getUsername());
        return set;
    }

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String usernameAsEmail) {
        log.info("== getCredentialIdsForUsername using a provided username hint (which is an email).");
        Set<CredentialRegistration> registrations;
        try {
            registrations = getRegistrationsByUsername(usernameAsEmail);
        } catch (UserNotFoundException e) {
            log.error("UserNotFoundException while getting registrations using username hint (email). Details: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        return registrations.stream()
                .map(reg -> {
                    log.debug("Mapping credentialId (first 8 bytes): {} for user associated with the provided email hint.",
                            Base64.getEncoder().encodeToString(Arrays.copyOf(reg.getCredential().getCredentialId().getBytes(), 8)));
                    return PublicKeyCredentialDescriptor.builder()
                            .id(reg.getCredential().getCredentialId())
                            .type(PublicKeyCredentialType.PUBLIC_KEY)
                            // TODO LOOK INTO UNDERSTANDING AND IMPLEMENTING TRANSPORTS
                            // Optionally include transports if you have them, e.g.:
                            // .transports(reg.getTransports())
                            .build();
                })
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String emailAsUsername) {
        log.info("== getUserHandleForUsername using a provided username hint (which is an email).");
        UsersEntity user = userRepository.findUserByEmail(emailAsUsername).orElseThrow(() -> {
            log.warn("User not found using the provided email hint during getUserHandleForUsername.");
            return new IllegalArgumentException("User does not exist for the provided email hint.");
        });
        return Optional.ofNullable(user).map(u -> {
            try {
                log.debug("Generating user handle for user: {}", u.getUsername());
                return generateUserHandle(u);
            } catch (Exception e) {
                log.error("Exception occurred when attempting to generate user handle {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }); // Use the same generation logic
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        log.info("== getUsernameForUserHandle for userHandle (first 8 bytes): {}",
                userHandle != null ? Base64.getEncoder().encodeToString(Arrays.copyOf(userHandle.getBytes(), 8)) : "N/A");

        log.debug("Attempting to find passkey by userHandle (first 8 bytes): {}",
                userHandle != null ? Base64.getEncoder().encodeToString(Arrays.copyOf(userHandle.getBytes(), 8)) : "N/A");
        try {
            Optional<PasskeySummary> optionalPasskeySummary = passkeyCredentialRepository.findPasskeyByUserHandle(userHandle.getBytes());
            if (optionalPasskeySummary.isPresent()) {
                PasskeySummary passkeySummary = optionalPasskeySummary.get();
                UsersEntity user = userRepository.findUserById(passkeySummary.getUserId());
                // Log the application's username, not the email (which Yubico uses as 'name')
                log.info("Found application username {} for userHandle (first 8 bytes): {}", user.getUsername(),
                        userHandle != null ? Base64.getEncoder().encodeToString(Arrays.copyOf(userHandle.getBytes(), 8)) : "N/A");
                return Optional.of(user.getEmail()); // Still return email as per Yubico's contract, just don't log it directly as "username"
            } else {
                log.info("No passkey summary found for userHandle (first 8 bytes): {}",
                        userHandle != null ? Base64.getEncoder().encodeToString(Arrays.copyOf(userHandle.getBytes(), 8)) : "N/A");
                return Optional.empty();
            }
        } catch (Exception e) { // Catch broader exceptions if userHandle is malformed or causes issues
            log.error("Error retrieving username for userHandle (first 8 bytes): {}. Error: {}",
                    userHandle != null ? Base64.getEncoder().encodeToString(Arrays.copyOf(userHandle.getBytes(), 8)) : "N/A", e.getMessage(), e);
            return Optional.empty();
        }
    }

    public Set<CredentialRegistration> getRegistrationsByUserHandle(ByteArray userHandle) {
        log.info("== getRegistrationsByUserHandle for userHandle (first 8 bytes): {}",
                userHandle != null ? Base64.getEncoder().encodeToString(Arrays.copyOf(userHandle.getBytes(), 8)) : "N/A");
        Set<CredentialRegistration> credentialRegistrations = getUsernameForUserHandle(userHandle)
                .map(email -> {
                    try {
                        return getRegistrationsByUsername(email);
                    } catch (UserNotFoundException e) {
                        log.error("User not found for email derived from userHandle (first 8 bytes: {}). Error: {}",
                                userHandle != null ? Base64.getEncoder().encodeToString(Arrays.copyOf(userHandle.getBytes(), 8)) : "N/A", e.getMessage(), e);
                        throw new RuntimeException(e); // Or handle more gracefully
                    }
                })
                .orElse(Set.of());
        log.info("Found {} registrations for userHandle (first 8 bytes): {}", credentialRegistrations.size(),
                userHandle != null ? Base64.getEncoder().encodeToString(Arrays.copyOf(userHandle.getBytes(), 8)) : "N/A");
        return credentialRegistrations;
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        log.info("== lookup for credentialId (first 8 bytes): {} and userHandle (first 8 bytes): {}",
                credentialId != null ? Base64.getEncoder().encodeToString(Arrays.copyOf(credentialId.getBytes(), 8)) : "N/A",
                userHandle != null ? Base64.getEncoder().encodeToString(Arrays.copyOf(userHandle.getBytes(), 8)) : "N/A");

        Optional<PasskeySummary> optionalPasskeySummary = passkeyCredentialRepository.findPasskeyByCredentialId(credentialId.getBytes());
        if (optionalPasskeySummary.isPresent()) {
            PasskeySummary passkeySummary = optionalPasskeySummary.get();
            log.debug("Found passkey summary for credentialId (first 8 bytes): {}", Base64.getEncoder().encodeToString(Arrays.copyOf(passkeySummary.getCredentialId(), 8)));

            RegisteredCredential registeredCredential = RegisteredCredential.builder()
                    .credentialId(new ByteArray(passkeySummary.getCredentialId()))
                    .userHandle(new ByteArray(passkeySummary.getUserHandle()))
                    .publicKeyCose(new ByteArray(passkeySummary.getPublicKeyCose()))
                    .signatureCount(passkeySummary.getSignatureCount())
                    .build();
            log.info("Successfully looked up RegisteredCredential for credentialId (first 8 bytes): {}", Base64.getEncoder().encodeToString(Arrays.copyOf(credentialId.getBytes(), 8)));
            return Optional.of(registeredCredential);
        } else {
            log.warn("No passkey summary found for credentialId (first 8 bytes): {}",
                    credentialId != null ? Base64.getEncoder().encodeToString(Arrays.copyOf(credentialId.getBytes(), 8)) : "N/A");
            // Consider if throwing an exception is appropriate or if an empty Optional is the contract.
            // For Yubico's library, an empty Optional is usually expected if not found.
            return Optional.empty();
        }
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray userHandle) {
        log.info("== lookupAll for userHandle (first 8 bytes): {}",
                userHandle != null ? Base64.getEncoder().encodeToString(Arrays.copyOf(userHandle.getBytes(), 8)) : "N/A");
        return getRegistrationsByUserHandle(userHandle).stream()
                .map(CredentialRegistration::getCredential)
                .collect(Collectors.toSet());
    }

    private CredentialRegistration toCredentialRegistration(
            PasskeySummary passkeySummary
    ) throws Base64UrlException {
        log.debug("== toCredentialRegistration for PasskeySummary with credentialId (first 8 bytes): {}",
                passkeySummary != null && passkeySummary.getCredentialId() != null ?
                        Base64.getEncoder().encodeToString(Arrays.copyOf(passkeySummary.getCredentialId(), 8)) : "N/A");

        UsersEntity user = userRepository.findUserById(passkeySummary.getUserId());
        log.debug("User {} found for passkey summary.", user.getUsername());

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
}

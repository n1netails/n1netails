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

    public Set<CredentialRegistration> getRegistrationsByUsername(String email) throws UserNotFoundException {
        log.info("== getRegistrationsByUsername: {}", email);
        UsersEntity user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user not found for passkey getRegistrationsByUsername"));
        if (user == null) return Set.of();

        Set<CredentialRegistration> set = new HashSet<>();
        log.info("== attempting to run for loop in findPasskeyByUserIdForUserRegistration(user.getId())");
        for (PasskeySummary passkeySummary : passkeyCredentialRepository.findPasskeyByUserIdForUserRegistration(user.getId())) {
            CredentialRegistration credentialRegistration = null;
            try {
                credentialRegistration = toCredentialRegistration(passkeySummary);
            } catch (Base64UrlException e) {
                log.error("Error occurred Generating Credential Registration by Username {}", e.getMessage());
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
                log.error("Exception occurred when attempting to generate user handle {}", e.getMessage());
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
            Optional<PasskeySummary> optionalPasskeySummary = passkeyCredentialRepository.findPasskeyByUserHandle(userHandle.getBytes());
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
                        log.error("User not found when attempting to get registration by user handle {}", e.getMessage());
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
        Optional<PasskeySummary> optionalPasskeySummary = passkeyCredentialRepository.findPasskeyByCredentialId(credentialId.getBytes());
        if (optionalPasskeySummary.isPresent()) {
            log.info("passkey summary: {}", optionalPasskeySummary.get());
            PasskeySummary passkeySummary = optionalPasskeySummary.get();
            log.info("Credential ID: {}", Base64.getUrlEncoder().encodeToString(passkeySummary.getCredentialId()));

            RegisteredCredential registeredCredential = RegisteredCredential.builder()
                    .credentialId(new ByteArray(passkeySummary.getCredentialId()))
                    .userHandle(new ByteArray(passkeySummary.getUserHandle()))
                    .publicKeyCose(new ByteArray(passkeySummary.getPublicKeyCose()))
                    .signatureCount(passkeySummary.getSignatureCount())
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

        UsersEntity user = userRepository.findUserById(passkeySummary.getUserId());

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

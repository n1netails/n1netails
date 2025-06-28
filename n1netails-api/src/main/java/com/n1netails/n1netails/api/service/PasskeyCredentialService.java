//package com.n1netails.n1netails.api.service;
//
//import com.n1netails.n1netails.api.model.dto.passkey.CredentialRegistration;
//import com.n1netails.n1netails.api.model.entity.PasskeyCredentialEntity;
//import com.n1netails.n1netails.api.model.entity.UsersEntity;
//import com.n1netails.n1netails.api.repository.PasskeyCredentialRepository;
//import com.n1netails.n1netails.api.repository.UserRepository;
//import com.yubico.webauthn.AssertionResult;
//import com.yubico.webauthn.CredentialRepository;
//import com.yubico.webauthn.RegisteredCredential;
//import com.yubico.webauthn.data.ByteArray;
//import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
//import com.yubico.webauthn.data.PublicKeyCredentialType;
//import com.yubico.webauthn.data.UserIdentity;
//import com.yubico.webauthn.data.exception.Base64UrlException;
//import lombok.NonNull;
//import lombok.RequiredArgsConstructor;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.time.Instant;
//import java.util.Date;
//import java.util.Optional;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class  PasskeyCredentialService implements CredentialRepository {
//
//    private final UserRepository userRepository;
//    private final PasskeyCredentialRepository passkeyCredentialRepository;
//
//    @SneakyThrows
//    public Set<CredentialRegistration> getRegistrationsByUsername(String username) {
//        UsersEntity user = userRepository.findUserByUsername(username);
//        if (user == null) return Set.of();
//
//        return passkeyCredentialRepository.findAllByUser(user).stream()
//                .map(this::toCredentialRegistration)
//                .collect(Collectors.toSet());
//    }
//
//    @SneakyThrows
//    public Optional<CredentialRegistration> getRegistrationByUsernameAndCredentialId(String username, ByteArray credentialId) {
//        UsersEntity user = userRepository.findUserByUsername(username);
//        if (user == null) {
//            return Optional.empty();
//        }
//        return passkeyCredentialRepository.findByUserAndCredentialId(user, credentialId)
//                .map(this::toCredentialRegistration);
//    }
//
//
//    @Override
//    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
//        // Fetch all registrations for the username
//        Set<CredentialRegistration> registrations = getRegistrationsByUsername(username);
//
//        // Map each registration's credential to a PublicKeyCredentialDescriptor
//        return registrations.stream()
//                .map(reg -> PublicKeyCredentialDescriptor.builder()
//                        .id(reg.getCredential().getCredentialId())
//                        .type(PublicKeyCredentialType.PUBLIC_KEY)
//                        // Optionally include transports if you have them, e.g.:
//                        // .transports(reg.getTransports())
//                        .build())
//                .collect(Collectors.toSet());
//    }
//
//    @Override
//    public Optional<ByteArray> getUserHandleForUsername(String username) {
//        UsersEntity user = userRepository.findUserByUsername(username);
//        return Optional.ofNullable(user).map(u -> {
//            try {
//                return generateUserHandle(u);
//            } catch (Base64UrlException e) {
//                throw new RuntimeException(e);
//            }
//        }); // Use the same generation logic
//    }
//
//    @Override
//    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
//        // This is the tricky part: mapping userHandle back to username.
//        // Since we're using user.getId().toString() as base64url for userHandle:
//        try {
//            Long userId = Long.parseLong(userHandle.getBase64Url()); // Assuming userHandle is base64url of the Long ID.
//            // Actually, if it's fromBase64Url(id.toString()), then it is id.toString().
//            // So, it should be new ByteArray(id.toString().getBytes(StandardCharsets.UTF_8)).getBase64Url()
//            // and to reverse: new String(userHandle.getBytes(), StandardCharsets.UTF_8)
//            // For simplicity, let's assume userHandle in DB is stored as the user.getId().toString()
//
//            // Correct approach: The userHandle stored in PasskeyCredentialEntity should be used for lookup.
//            // The userHandle in PasskeyCredentialEntity is `creationOptions.getUser().getId().getBase64Url()`
//            // which is `generateUserHandle(user).getBase64Url()` which is `ByteArray.fromBase64Url(user.getId().toString()).getBase64Url()`
//            // This means the stored userHandle is effectively user.getId().toString().
//            String userIdStr = userHandle.getBase64Url(); // This is user.getId().toString()
//
//            return passkeyCredentialRepository.findAll().stream() // Inefficient: Iterate all credentials
//                    .filter(cred -> cred.getUserHandle() != null && cred.getUserHandle().equals(userIdStr))
//                    .map(cred -> cred.getUser().getUsername())
//                    .findFirst();
//
//        } catch (NumberFormatException e) {
//            log.error("Could not parse user ID from userHandle: {}", userHandle.getBase64Url(), e);
//            return Optional.empty();
//        }
//    }
//
//    public Set<CredentialRegistration> getRegistrationsByUserHandle(ByteArray userHandle) {
//        // Find username from userHandle, then call getRegistrationsByUsername
//        return getUsernameForUserHandle(userHandle)
//                .map(this::getRegistrationsByUsername)
//                .orElse(Set.of());
//    }
//
//    // This method is used by Yubico library to look up a specific credential.
//    // It's crucial for authentication.
//    @Override
//    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
//        // The userHandle parameter here is the one provided by the authenticator during assertion.
//        // It should match the userHandle stored with the credential.
//        return passkeyCredentialRepository.findByCredentialId(credentialId)
//                .filter(cred -> cred.getUserHandle() != null && cred.getUserHandle().equals(userHandle.getBase64Url()))
//                .map(credEntity -> RegisteredCredential.builder()
//                        .credentialId(new ByteArray(credEntity.getCredentialId()))
//                        .userHandle(new ByteArray(credEntity.getUserHandle()))
//                        .publicKeyCose(new ByteArray(credEntity.getPublicKeyCose()))
//                        .signatureCount(credEntity.getSignatureCount())
//                        .build());
//    }
//
//    @Override
//    public Set<RegisteredCredential> lookupAll(ByteArray userHandle) {
//        return getRegistrationsByUserHandle(userHandle).stream()
//                .map(reg -> reg.getCredential()) // CredentialRegistration contains RegisteredCredential
//                .collect(Collectors.toSet());
//    }
//
//    // This method is called by the library to persist a new credential.
//    // Since we save manually in finishRegistration, this might not be strictly needed
//    // if all library paths lead to our manual save. However, it's good practice to implement.
//    public boolean addRegistration(RegisteredCredential registration) {
//        log.warn("JpaCredentialRepository.addRegistration called. This indicates an unexpected flow or misconfiguration, as credentials should be saved via PasskeyService.finishRegistration.");
//        // To prevent potential issues, avoid saving here if finishRegistration is the primary path.
//        // If this method IS indeed part of a flow you intend to use, you'd implement the logic
//        // to convert RegisteredCredential to PasskeyCredentialEntity and save it.
//        // This would involve looking up the user by userHandle.
//        return false; // Indicate it wasn't (or shouldn't be) handled here.
//    }
//
//    public boolean removeRegistration(ByteArray userHandle, ByteArray credentialId) {
//        // Implementation for deleting a credential
//        Optional<String> usernameOpt = getUsernameForUserHandle(userHandle);
//        if (usernameOpt.isEmpty()) {
//            log.warn("Cannot remove registration: User not found for userHandle {}", userHandle.getBase64Url());
//            return false;
//        }
//        UsersEntity user = userRepository.findUserByUsername(usernameOpt.get());
//        if (user == null) { // Should not happen if usernameOpt was present
//            log.warn("Cannot remove registration: User {} (from handle {}) not found in DB", usernameOpt.get(), userHandle.getBase64Url());
//            return false;
//        }
//
//        Optional<PasskeyCredentialEntity> cred = passkeyCredentialRepository.findByUserAndCredentialId(user, credentialId);
//        if (cred.isPresent()) {
//            passkeyCredentialRepository.delete(cred.get());
//            log.info("Removed passkey credential {} for user {}", credentialId.getBase64Url(), user.getUsername());
//            return true;
//        } else {
//            log.warn("Passkey credential {} not found for user {} during removal attempt.", credentialId.getBase64Url(), user.getUsername());
//            return false;
//        }
//    }
//
//
//    // This method is crucial and is called by the library after a successful assertion.
//    public boolean updateSignatureCount(AssertionResult assertionResult) {
//        Optional<PasskeyCredentialEntity> credEntityOpt = passkeyCredentialRepository.findByCredentialId(assertionResult.getCredentialId());
//        if (credEntityOpt.isPresent()) {
//            PasskeyCredentialEntity credEntity = credEntityOpt.get();
//            if (credEntity.getUserHandle().equals(assertionResult.getUserHandle().getBase64Url())) { // Ensure it's the correct user's credential
//                credEntity.setSignatureCount(assertionResult.getSignatureCount());
//                credEntity.setLastUsedAt(new Date()); // Also update last used timestamp
//                passkeyCredentialRepository.save(credEntity);
//                log.debug("Updated signature count for credential {} to {}", assertionResult.getCredentialId().getBase64Url(), assertionResult.getSignatureCount());
//                return true;
//            } else {
//                log.warn("Attempted to update signature count for credential {} but user handle did not match. DB: {}, Assertion: {}", assertionResult.getCredentialId().getBase64Url(), credEntity.getUserHandle(), assertionResult.getUserHandle().getBase64Url());
//                return false;
//            }
//        } else {
//            log.warn("Could not find credential {} to update signature count.", assertionResult.getCredentialId().getBase64Url());
//            return false;
//        }
//    }
//
//    private CredentialRegistration toCredentialRegistration(PasskeyCredentialEntity entity) {
//        // Ensure user handle used here is what the library expects (usually the one from UserIdentity)
//        UserIdentity userIdentity = null;
//        userIdentity = UserIdentity.builder()
//                .name(entity.getUser().getUsername())
//                .displayName(entity.getUser().getFirstName() + " " + entity.getUser().getLastName())
//                .id(new ByteArray(entity.getUserHandle())) // This MUST match the userHandle stored on the entity
//                .build();
//
//        return CredentialRegistration.builder()
//                .credential(RegisteredCredential.builder()
//                        .credentialId(new ByteArray(entity.getCredentialId()))
//                        .userHandle(userIdentity.getId()) // Use the same ID as in UserIdentity
//                        .publicKeyCose(new ByteArray(entity.getPublicKeyCose()))
//                        .signatureCount(entity.getSignatureCount())
//                        .build())
//                .userIdentity(userIdentity)
//                .registrationTime(entity.getRegisteredAt() != null ? entity.getRegisteredAt().toInstant() : Instant.now())
//                // Optional: Add transports if your library version/config uses them here
//                // .transports(entity.getTransports().stream().map(AuthenticatorTransport::of).collect(Collectors.toSet()))
//                .build();
//    }
//
//    // === UTILITY METHODS ===
//    private @NonNull ByteArray generateUserHandle(UsersEntity user) throws Base64UrlException {
//        // User handle MUST be stable and unique for the user.
//        // It should NOT be PII if possible, and MUST NOT change for a given user.
//        // Using user.getUserId() (which seems to be a string based on UsersEntity) is a good candidate if it's stable and unique.
//        // The Yubico library expects a ByteArray. Max 64 bytes.
//        // Ensure it's at least 1 byte, preferably more for uniqueness.
//        String userId = user.getUserId(); // This is a String, e.g. "5107983742" based on generateUserId()
//        if (userId == null || userId.isBlank()) {
//            // This should not happen for an existing user
//            log.error("User ID is null or blank for user: {}. Cannot generate user handle.", user.getUsername());
//            throw new IllegalArgumentException("User ID cannot be null or blank for user handle generation.");
//        }
//        // Let's use the string representation of the user's primary ID (Long) as the user handle.
//        // This is stable and unique.
//        return ByteArray.fromBase64Url(user.getId().toString()); // Using DB ID (Long) for stability
//    }
//}

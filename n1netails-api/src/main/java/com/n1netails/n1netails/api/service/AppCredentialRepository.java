package com.n1netails.n1netails.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1netails.n1netails.api.model.entity.PasskeyCredentialEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.repository.PasskeyCredentialRepository;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.data.UserIdentity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service; // Should be a component managed by Spring for injection

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor // Not a @Service itself, but instantiated in WebAuthnConfig
public class AppCredentialRepository implements CredentialRepository {

    private final PasskeyCredentialRepository passkeyCredentialRepository;
    private final ObjectMapper objectMapper; // For potential JSON conversion if needed for extra fields, not directly used by Yubico interface methods

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        // This method is a bit tricky as our PasskeyCredentialEntity is linked to UsersEntity by ID, not directly by username string
        // And Yubico's CredentialRepository interface uses username string.
        // For now, this implies that the username in Yubico's context IS the userHandle or we need another way to link them.
        // Let's assume for now that userHandle is what Yubico expects as "username" in this context for lookup.
        // This might need adjustment based on how UserIdentity.name is populated during registration.
        // If UserIdentity.name is the user's primary email/login username, we'd need to fetch the user by that, then their credentials.
        // For simplicity, let's assume the 'username' parameter here refers to the userHandle.
        log.debug("Getting credential IDs for userHandle: {}", username);
        return passkeyCredentialRepository.findByUserHandle(username).stream()
                .map(cred -> PublicKeyCredentialDescriptor.builder()
                        .id(ByteArray.fromBase64Url(cred.getExternalId()))
                        .build())
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        // This method is called by Yubico lib to get a persistent, non-personally identifiable user handle.
        // We store userHandle in PasskeyCredentialEntity. If the 'username' parameter is the actual login username,
        // we should fetch the UserEntity, then get its userHandle (which should be consistent across credentials for that user).
        // Or, if we decide UserIdentity.name used in registration *is* the userHandle, this is simpler.
        // Let's assume the 'username' parameter here is the user's login username.
        // We need a way to get a consistent user_handle for a given UsersEntity.
        // For now, we'll query one of their passkeys and return its user_handle. This assumes all passkeys for a user share the same user_handle.
        log.debug("Getting user handle for username (interpreted as login username): {}", username);
        return passkeyCredentialRepository.findByUser_Email(username) // Need to add findByUser_Email to repository
                .stream()
                .findFirst()
                .map(PasskeyCredentialEntity::getUserHandle)
                .map(ByteArray::fromBase64Url); // Assuming userHandle is stored Base64URL encoded. If not, adjust.
                                                // Yubico's UserIdentity.id is a ByteArray.
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        // This should return the "login username" (e.g., email) associated with the given userHandle.
        // We find a credential by userHandle, then get the associated UsersEntity's email.
        log.debug("Getting username for user handle: {}", userHandle.getBase64Url());
        return passkeyCredentialRepository.findByUserHandle(userHandle.getBase64Url())
                .stream()
                .findFirst()
                .map(PasskeyCredentialEntity::getUser)
                .map(UsersEntity::getEmail);
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        // Lookup a specific credential by its ID and the userHandle it was registered with.
        log.debug("Looking up credential ID: {}, user handle: {}", credentialId.getBase64Url(), userHandle.getBase64Url());
        return passkeyCredentialRepository.findByExternalIdAndUserHandle(credentialId.getBase64Url(), userHandle.getBase64Url()) // Need to add this method
                .map(this::convertToRegisteredCredential);
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        // Lookup all credentials that match a given credential ID.
        // This is usually for discovering all users who might have registered a specific roaming authenticator.
        log.debug("Looking up all credentials for ID: {}", credentialId.getBase64Url());
        return passkeyCredentialRepository.findByExternalId(credentialId.getBase64Url()).stream()
                .map(this::convertToRegisteredCredential)
                .collect(Collectors.toSet());
    }

    private RegisteredCredential convertToRegisteredCredential(PasskeyCredentialEntity entity) {
        return RegisteredCredential.builder()
                .credentialId(ByteArray.fromBase64Url(entity.getExternalId()))
                .userHandle(ByteArray.fromBase64Url(entity.getUserHandle())) // Assuming userHandle is stored Base64URL
                .publicKeyCose(ByteArray.fromBase64Url(entity.getPublicKeyCose()))
                .signatureCount(entity.getCount())
                .build();
    }

    // Helper method to add to PasskeyCredentialRepository if it doesn't exist
    // public List<PasskeyCredentialEntity> findByUser_Email(String email) - this would actually go in the repository interface
}

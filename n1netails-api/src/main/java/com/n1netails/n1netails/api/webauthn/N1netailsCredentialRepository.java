package com.n1netails.n1netails.api.webauthn;

import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.n1netails.n1netails.api.model.entity.UserAuthenticator; // This will be a new JPA entity
import com.n1netails.n1netails.api.repository.UserAuthenticatorRepository; // New Spring Data JPA repository
import com.n1netails.n1netails.api.repository.UserRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class N1netailsCredentialRepository implements CredentialRepository {

    private final UserAuthenticatorRepository userAuthenticatorRepository;
    private final UserRepository userRepository;

    public N1netailsCredentialRepository(
            UserAuthenticatorRepository userAuthenticatorRepository,
            UserRepository userRepository) {
        this.userAuthenticatorRepository = userAuthenticatorRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        Optional<UsersEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            return userAuthenticatorRepository.findAllByUserId(userOptional.get().getId())
                .stream()
                .map(auth -> PublicKeyCredentialDescriptor.builder()
                    .id(auth.getCredentialId())
                    .build())
                .collect(Collectors.toSet());
        }
        return Set.of();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        return userRepository.findByUsername(username)
            .map(user -> user.getUserHandle()); // Assuming User entity has a getUserHandle() that returns ByteArray
                                                // This might need to be the user's ID or a specific handle for WebAuthn
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        // This implies that userHandle is unique and can be used to find a user.
        // This might require a new field on the User entity or UserAuthenticator entity.
        // For now, let's assume userHandle is the string representation of User's UUID.
        // This needs careful consideration based on how user handles are generated and stored.
        try {
            // Attempt to find user by user_handle if stored directly
            return userAuthenticatorRepository.findByUserHandle(userHandle)
                .map(UserAuthenticator::getUser)
                .map(UsersEntity::getUsername);
            // Or, if userHandle is simply the user's ID as bytes:
            // String userIdStr = new String(userHandle.getBytes());
            // return userRepository.findById(UUID.fromString(userIdStr)).map(User::getUsername);
        } catch (Exception e) {
            // Log error: Failed to convert userHandle to username
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        return userAuthenticatorRepository.findByCredentialIdAndUserHandle(credentialId, userHandle)
            .map(auth -> RegisteredCredential.builder()
                .credentialId(auth.getCredentialId())
                .userHandle(auth.getUserHandle()) // Make sure UserAuthenticator stores this
                .publicKeyCose(auth.getPublicKey()) // Assuming publicKey is stored in COSE format
                .signatureCount(auth.getSignCount())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        return userAuthenticatorRepository.findAllByCredentialId(credentialId)
            .stream()
            .map(auth -> RegisteredCredential.builder()
                .credentialId(auth.getCredentialId())
                .userHandle(auth.getUserHandle())
                .publicKeyCose(auth.getPublicKey())
                .signatureCount(auth.getSignCount())
                .build())
            .collect(Collectors.toSet());
    }

    // Method to save a new authenticator (called by PasskeyServiceImpl)
    @Transactional
    public void saveAuthenticator(UserAuthenticator authenticator) {
        userAuthenticatorRepository.save(authenticator);
    }

    // Method to update an authenticator (e.g., sign count, called by PasskeyServiceImpl)
    @Transactional
    public void updateAuthenticator(UserAuthenticator authenticator) {
        // Ensure the authenticator exists before updating
        if (userAuthenticatorRepository.existsById(authenticator.getId())) {
            userAuthenticatorRepository.save(authenticator);
        } else {
            // Handle error: authenticator not found
        }
    }
     @Transactional(readOnly = true)
    public Optional<UserAuthenticator> findByCredentialId(ByteArray credentialId) {
        return userAuthenticatorRepository.findByCredentialId(credentialId);
    }
}

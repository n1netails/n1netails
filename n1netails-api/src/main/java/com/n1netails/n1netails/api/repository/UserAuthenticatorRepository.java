package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.UserAuthenticator;
import com.yubico.webauthn.data.ByteArray;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserAuthenticatorRepository extends JpaRepository<UserAuthenticator, UUID> {

    Set<UserAuthenticator> findAllByUserId(Long userId);

    // For CredentialRepository:
    // Optional<ByteArray> getUserHandleForUsername(String username);
    // This is tricky because UserAuthenticator doesn't directly link to username, but to User.
    // This logic might be better placed in a service that combines UserRepository and UserAuthenticatorRepository.
    // For now, N1netailsCredentialRepository will handle this by first fetching the user.

    // Optional<String> getUsernameForUserHandle(ByteArray userHandle);
    // Similar to above, this needs User context.
    @Query("SELECT ua FROM UserAuthenticator ua WHERE ua.userHandleBytes = :userHandle")
    Optional<UserAuthenticator> findByUserHandle(@Param("userHandle") ByteArray userHandle);


    @Query("SELECT ua FROM UserAuthenticator ua WHERE ua.credentialIdBytes = :credentialId AND ua.userHandleBytes = :userHandle")
    Optional<UserAuthenticator> findByCredentialIdAndUserHandle(@Param("credentialId") ByteArray credentialId, @Param("userHandle") ByteArray userHandle);

    @Query("SELECT ua FROM UserAuthenticator ua WHERE ua.credentialIdBytes = :credentialId")
    Set<UserAuthenticator> findAllByCredentialId(@Param("credentialId") ByteArray credentialId);

    @Query("SELECT ua FROM UserAuthenticator ua WHERE ua.credentialIdBytes = :credentialId")
    Optional<UserAuthenticator> findByCredentialId(@Param("credentialId") ByteArray credentialId);

    void deleteByCredentialId(ByteArray credentialId); // For managing credentials

    boolean existsByCredentialId(ByteArray credentialId);
}

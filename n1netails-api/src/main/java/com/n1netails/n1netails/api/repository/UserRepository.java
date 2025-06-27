package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.yubico.webauthn.data.ByteArray;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UsersEntity, Long> {

    Optional<UsersEntity> findUserByEmail(String email);

    UsersEntity findUserById(Long userId);

    // Keep existing findByUsername, ensure it returns Optional for consistency
    Optional<UsersEntity> findByUsername(String username);

    // Add method to find by userHandle (byte array)
    @Query("SELECT u FROM UsersEntity u WHERE u.userHandleBytes = :userHandleBytes")
    Optional<UsersEntity> findByUserHandleBytes(@Param("userHandleBytes") byte[] userHandleBytes);

    // Overload for existing findUserByUsername for compatibility if needed, or refactor usages
    // For clarity, it's better to use findByUsername which returns Optional.
    // UsersEntity findUserByUsername(String username); // This can be removed if all usages are updated
}

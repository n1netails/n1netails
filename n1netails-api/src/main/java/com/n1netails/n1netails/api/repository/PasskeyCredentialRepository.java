package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.PasskeyCredentialEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasskeyCredentialRepository extends JpaRepository<PasskeyCredentialEntity, Long> {

    Optional<PasskeyCredentialEntity> findByExternalId(String externalId);

    List<PasskeyCredentialEntity> findByUser(UsersEntity user);

    List<PasskeyCredentialEntity> findByUserHandle(String userHandle);

    boolean existsByUserAndFriendlyName(UsersEntity user, String friendlyName);

    // Added for AppCredentialRepository
    List<PasskeyCredentialEntity> findByUser_Email(String email);

    Optional<PasskeyCredentialEntity> findByExternalIdAndUserHandle(String externalId, String userHandle);
}

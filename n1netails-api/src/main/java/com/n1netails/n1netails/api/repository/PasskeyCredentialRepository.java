package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.PasskeyCredentialEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasskeyCredentialRepository extends JpaRepository<PasskeyCredentialEntity, Long> {

    Optional<PasskeyCredentialEntity> findByCredentialId(String credentialId);

    List<PasskeyCredentialEntity> findAllByUser(UsersEntity user);

    Optional<PasskeyCredentialEntity> findByUserAndCredentialId(UsersEntity user, String credentialId);
}

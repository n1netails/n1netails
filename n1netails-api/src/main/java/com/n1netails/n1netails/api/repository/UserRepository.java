package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UsersEntity, Long> {

    Optional<UsersEntity> findUserByEmail(String email);

    UsersEntity findUserById(Long userId);

    UsersEntity findUserByUsername(String username);

    Optional<UsersEntity> findByProviderAndProviderId(String provider, String providerId);
}

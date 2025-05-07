package com.n1ne.n1netails.api.repository;

import com.n1ne.n1netails.api.model.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findUserByEmail(String email);

    Users findUserByUserId(String userId);

    Users findUserByUsername(String username);
}

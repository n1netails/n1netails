package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.ForgotPasswordRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * ForgotPasswordRequestRepository
 */
@Repository
public interface ForgotPasswordRequestRepository extends JpaRepository<ForgotPasswordRequestEntity, UUID> {
}

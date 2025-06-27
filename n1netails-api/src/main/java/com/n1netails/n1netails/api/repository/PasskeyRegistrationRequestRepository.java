package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.PasskeyRegistrationRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface PasskeyRegistrationRequestRepository extends JpaRepository<PasskeyRegistrationRequestEntity, String> {

    Optional<PasskeyRegistrationRequestEntity> findByRequestId(String requestId);

    void deleteAllByCreatedAtBefore(OffsetDateTime expiryTime);
}

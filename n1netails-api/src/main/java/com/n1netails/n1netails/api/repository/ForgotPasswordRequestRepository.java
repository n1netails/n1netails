package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.ForgotPasswordRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * ForgotPasswordRequestRepository
 */
@Repository
public interface ForgotPasswordRequestRepository extends JpaRepository<ForgotPasswordRequestEntity, UUID> {
    @Query("SELECT rq FROM ForgotPasswordRequestEntity rq " +
            "WHERE rq.expiredAt <= :now")
    List<ForgotPasswordRequestEntity> findExpiredRequests(LocalDateTime now);
}

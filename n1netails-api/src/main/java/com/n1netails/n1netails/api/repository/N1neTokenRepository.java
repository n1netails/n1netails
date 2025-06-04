package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.N1neTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface N1neTokenRepository extends JpaRepository<N1neTokenEntity, Long> {

    List<N1neTokenEntity> findByUserId(Long userId);
    Optional<N1neTokenEntity> findByToken(UUID token);
    List<N1neTokenEntity> findByOrganizationId(Long organizationId);
    List<N1neTokenEntity> findByOrganizationIdIn(List<Long> organizationIds);
    Optional<N1neTokenEntity> findByIdAndUserId(Long id, Long userId);
    List<N1neTokenEntity> findByUserIdIn(List<Long> userIds);
    List<N1neTokenEntity> findByUserIdAndOrganizationIsNull(Long userId); // For Admin's getAll()
}

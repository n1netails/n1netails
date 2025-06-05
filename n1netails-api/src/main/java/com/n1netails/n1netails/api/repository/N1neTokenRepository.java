package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.N1neTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface N1neTokenRepository extends JpaRepository<N1neTokenEntity, Long> {

    List<N1neTokenEntity> findByUserId(Long userId);
    Optional<N1neTokenEntity> findByToken(UUID token);
    List<N1neTokenEntity> findByOrganization_IdIn(Set<Long> organizationIds); // New method
}

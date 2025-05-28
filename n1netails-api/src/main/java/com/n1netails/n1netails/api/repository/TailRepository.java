package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.TailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TailRepository extends JpaRepository<TailEntity, Long> {
    List<TailEntity> findByAssignedUserId(Long userId);

    List<TailEntity> findByTimestampBetween(Instant startOfDay, Instant endOfDay);
    long countByTimestampBetween(Instant startOfDay, Instant endOfDay);

    List<TailEntity> findAllByStatusName(String statusName);
    long countByStatusName(String statusName);

    List<TailEntity> findAllByStatusNameNot(String statusName);
    long countByStatusNameNot(String statusName);

    List<TailEntity> findAllByResolvedTimestampIsNotNull();
}

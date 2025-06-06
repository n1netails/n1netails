package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.dto.TailLevelAndTimestamp;
import com.n1netails.n1netails.api.model.dto.TailTimestampAndResolvedTimestamp;
import com.n1netails.n1netails.api.model.entity.TailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TailMetricsRepository extends JpaRepository<TailEntity, Long> {

    // Methods for filtering by UserId
    long countByTimestampBetweenAndAssignedUserId(Instant startOfDay, Instant endOfDay, Long userId);

    List<TailEntity> findByTimestampBetweenAndAssignedUserId(Instant startOfDay, Instant endOfDay, Long userId);

    List<TailEntity> findAllByStatusNameAndAssignedUserId(String statusName, Long userId);

    long countByStatusNameAndAssignedUserId(String statusName, Long userId);

    List<TailEntity> findAllByStatusNameNotAndAssignedUserId(String statusName, Long userId);

    long countByStatusNameNotAndAssignedUserId(String statusName, Long userId);

    @Query("SELECT new com.n1netails.n1netails.api.model.dto.TailTimestampAndResolvedTimestamp(t.timestamp, t.resolvedTimestamp) " +
            "FROM TailEntity t " +
            "WHERE t.resolvedTimestamp IS NOT NULL " +
            "AND t.assignedUserId = :userId")
    List<TailTimestampAndResolvedTimestamp> findOnlyTimestampAndResolvedTimestampIsNotNullAndUserId(@Param("userId") Long userId);

    @Query("SELECT t.timestamp " +
            "FROM TailEntity t " +
            "WHERE t.timestamp BETWEEN :start AND :end " +
            "AND t.assignedUserId = :userId " +
            "ORDER BY t.timestamp ASC")
    List<Instant> findOnlyTimestampsBetweenAndUserId(@Param("start") Instant start, @Param("end") Instant end, @Param("userId") Long userId);

    @Query("SELECT new com.n1netails.n1netails.api.model.dto.TailLevelAndTimestamp(t.timestamp, t.level) " +
            "FROM TailEntity t " +
            "WHERE t.timestamp BETWEEN :start AND :end " +
            "AND t.assignedUserId = :userId " +
            "ORDER BY t.timestamp ASC")
    List<TailLevelAndTimestamp> findOnlyLevelAndTimestampsBetweenAndUserId(@Param("start") Instant start, @Param("end") Instant end, @Param("userId") Long userId);

    @Query("SELECT new com.n1netails.n1netails.api.model.dto.TailTimestampAndResolvedTimestamp(t.timestamp, t.resolvedTimestamp) " +
            "FROM TailEntity t " +
            "WHERE t.resolvedTimestamp IS NOT NULL " +
            "AND t.timestamp >= :daysAgo " +
            "AND t.assignedUserId = :userId")
    List<TailTimestampAndResolvedTimestamp> findAllByTimestampAfterAndResolvedTimestampIsNotNullAndUserId(@Param("daysAgo") Instant daysAgo, @Param("userId") Long userId);

    // Methods for filtering by OrganizationIdIn
    @Query("SELECT COUNT(t) " +
            "FROM TailEntity t " +
            "JOIN t.organization o " +
            "WHERE t.timestamp BETWEEN :startOfDay AND :endOfDay " +
            "AND o.id IN :organizationIds")
    long countByTimestampBetweenAndOrganizationIdIn(@Param("startOfDay") Instant startOfDay, @Param("endOfDay") Instant endOfDay, @Param("organizationIds") List<Long> organizationIds);

    @Query("SELECT t " +
            "FROM TailEntity t " +
            "JOIN t.organization o " +
            "WHERE t.timestamp BETWEEN :startOfDay AND :endOfDay " +
            "AND o.id IN :organizationIds")
    List<TailEntity> findByTimestampBetweenAndOrganizationIdIn(@Param("startOfDay") Instant startOfDay, @Param("endOfDay") Instant endOfDay, @Param("organizationIds") List<Long> organizationIds);

    @Query("SELECT t " +
            "FROM TailEntity t " +
            "JOIN t.organization o " +
            "WHERE t.status.name = :statusName " +
            "AND o.id IN :organizationIds")
    List<TailEntity> findAllByStatusNameAndOrganizationIdIn(@Param("statusName") String statusName, @Param("organizationIds") List<Long> organizationIds);

    @Query("SELECT COUNT(t) " +
            "FROM TailEntity t " +
            "JOIN t.organization o " +
            "WHERE t.status.name = :statusName " +
            "AND o.id IN :organizationIds")
    long countByStatusNameAndOrganizationIdIn(@Param("statusName") String statusName, @Param("organizationIds") List<Long> organizationIds);

    @Query("SELECT t " +
            "FROM TailEntity t " +
            "JOIN t.organization o " +
            "WHERE t.status.name <> :statusName " +
            "AND o.id IN :organizationIds")
    List<TailEntity> findAllByStatusNameNotAndOrganizationIdIn(@Param("statusName") String statusName, @Param("organizationIds") List<Long> organizationIds);

    @Query("SELECT COUNT(t) " +
            "FROM TailEntity t " +
            "JOIN t.organization o " +
            "WHERE t.status.name <> :statusName " +
            "AND o.id IN :organizationIds")
    long countByStatusNameNotAndOrganizationIdIn(@Param("statusName") String statusName, @Param("organizationIds") List<Long> organizationIds);

    @Query("SELECT new com.n1netails.n1netails.api.model.dto.TailTimestampAndResolvedTimestamp(t.timestamp, t.resolvedTimestamp) " +
            "FROM TailEntity t " +
            "JOIN t.organization o " +
            "WHERE t.resolvedTimestamp IS NOT NULL " +
            "AND o.id IN :organizationIds")
    List<TailTimestampAndResolvedTimestamp> findOnlyTimestampAndResolvedTimestampIsNotNullAndOrganizationIdIn(@Param("organizationIds") List<Long> organizationIds);

    @Query("SELECT t.timestamp " +
            "FROM TailEntity t " +
            "JOIN t.organization o " +
            "WHERE t.timestamp BETWEEN :start AND :end " +
            "AND o.id IN :organizationIds " +
            "ORDER BY t.timestamp ASC")
    List<Instant> findOnlyTimestampsBetweenAndOrganizationIdIn(@Param("start") Instant start, @Param("end") Instant end, @Param("organizationIds") List<Long> organizationIds);

    @Query("SELECT new com.n1netails.n1netails.api.model.dto.TailLevelAndTimestamp(t.timestamp, t.level) " +
            "FROM TailEntity t " +
            "JOIN t.organization o " +
            "WHERE t.timestamp BETWEEN :start AND :end " +
            "AND o.id IN :organizationIds " +
            "ORDER BY t.timestamp ASC")
    List<TailLevelAndTimestamp> findOnlyLevelAndTimestampsBetweenAndOrganizationIdIn(@Param("start") Instant start, @Param("end") Instant end, @Param("organizationIds") List<Long> organizationIds);

    @Query("SELECT new com.n1netails.n1netails.api.model.dto.TailTimestampAndResolvedTimestamp(t.timestamp, t.resolvedTimestamp) " +
            "FROM TailEntity t " +
            "JOIN t.organization o " +
            "WHERE t.resolvedTimestamp IS NOT NULL " +
            "AND t.timestamp >= :daysAgo " +
            "AND o.id IN :organizationIds")
    List<TailTimestampAndResolvedTimestamp> findAllByTimestampAfterAndResolvedTimestampIsNotNullAndOrganizationIdIn(@Param("daysAgo") Instant daysAgo, @Param("organizationIds") List<Long> organizationIds);
}

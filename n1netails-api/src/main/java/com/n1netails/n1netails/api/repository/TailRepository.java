package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.dto.TailLevelAndTimestamp;
import com.n1netails.n1netails.api.model.dto.TailSummary;
import com.n1netails.n1netails.api.model.dto.TailTimestampAndResolvedTimestamp;
import com.n1netails.n1netails.api.model.entity.TailEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface TailRepository extends JpaRepository<TailEntity, Long>, JpaSpecificationExecutor<TailEntity> {

    // Added for getTop9NewestTails authorization logic
    List<TailEntity> findByOrganization_IdInAndUser_IdOrderByCreatedAtDesc(Collection<Long> organizationIds, Long userId, Pageable pageable);
    List<TailEntity> findByOrganization_IdInOrderByCreatedAtDesc(Collection<Long> organizationIds, Pageable pageable);

    @Query("""
        SELECT new com.n1netails.n1netails.api.model.dto.TailSummary(
            t.id,
            t.title,
            t.description,
            t.timestamp,
            t.resolvedTimestamp,
            t.assignedUserId,
            l.name,
            ty.name,
            s.name
        )
        FROM TailEntity t
        JOIN t.level l
        JOIN t.type ty
        JOIN t.status s
        WHERE t.resolvedTimestamp IS NULL
        ORDER BY t.timestamp DESC
    """)
    Page<TailSummary> findAllByOrderByTimestampDesc(Pageable pageable);

    @Query("""
        SELECT new com.n1netails.n1netails.api.model.dto.TailSummary(
            t.id,
            t.title,
            t.description,
            t.timestamp,
            t.resolvedTimestamp,
            t.assignedUserId,
            l.name,
            ty.name,
            s.name
        )
        FROM TailEntity t
        JOIN t.level l
        JOIN t.type ty
        JOIN t.status s
        WHERE s.name IN :statuses
        AND ty.name IN :types
        AND l.name IN :levels
        AND (t.title LIKE %:searchTerm% OR t.description LIKE %:searchTerm%)
        ORDER BY t.timestamp DESC
    """)
    Page<TailSummary> findAllBySearchTermAndTailFilters(
            @Param("searchTerm") String searchTerm,
            @Param("statuses") List<String> statuses,
            @Param("types") List<String> types,
            @Param("levels") List<String> levels,
            Pageable pageable
    );

    List<TailEntity> findByAssignedUserId(Long userId);

    // Methods for TailMetricsService authorization
    @Query("SELECT t FROM TailEntity t WHERE t.timestamp BETWEEN :startOfDay AND :endOfDay AND t.organization.id IN :orgIds AND (:userId IS NULL OR t.user.id = :userId)")
    List<TailEntity> findByTimestampBetweenAndOrganizationIdsAndOptionalUserId(
            @Param("startOfDay") Instant startOfDay,
            @Param("endOfDay") Instant endOfDay,
            @Param("orgIds") Set<Long> orgIds,
            @Param("userId") Long userId
    );

    @Query("SELECT COUNT(t) FROM TailEntity t WHERE t.timestamp BETWEEN :startOfDay AND :endOfDay AND t.organization.id IN :orgIds AND (:userId IS NULL OR t.user.id = :userId)")
    long countByTimestampBetweenAndOrganizationIdsAndOptionalUserId(
            @Param("startOfDay") Instant startOfDay,
            @Param("endOfDay") Instant endOfDay,
            @Param("orgIds") Set<Long> orgIds,
            @Param("userId") Long userId
    );

    @Query("SELECT t FROM TailEntity t WHERE t.status.name = :statusName AND t.organization.id IN :orgIds AND (:userId IS NULL OR t.user.id = :userId)")
    List<TailEntity> findAllByStatusNameAndOrganizationIdsAndOptionalUserId(
            @Param("statusName") String statusName,
            @Param("orgIds") Set<Long> orgIds,
            @Param("userId") Long userId
    );

    @Query("SELECT COUNT(t) FROM TailEntity t WHERE t.status.name = :statusName AND t.organization.id IN :orgIds AND (:userId IS NULL OR t.user.id = :userId)")
    long countByStatusNameAndOrganizationIdsAndOptionalUserId(
            @Param("statusName") String statusName,
            @Param("orgIds") Set<Long> orgIds,
            @Param("userId") Long userId
    );

    @Query("SELECT t FROM TailEntity t WHERE t.status.name <> :statusName AND t.organization.id IN :orgIds AND (:userId IS NULL OR t.user.id = :userId)")
    List<TailEntity> findAllByStatusNameNotAndOrganizationIdsAndOptionalUserId(
            @Param("statusName") String statusName,
            @Param("orgIds") Set<Long> orgIds,
            @Param("userId") Long userId
    );

    @Query("SELECT COUNT(t) FROM TailEntity t WHERE t.status.name <> :statusName AND t.organization.id IN :orgIds AND (:userId IS NULL OR t.user.id = :userId)")
    long countByStatusNameNotAndOrganizationIdsAndOptionalUserId(
            @Param("statusName") String statusName,
            @Param("orgIds") Set<Long> orgIds,
            @Param("userId") Long userId
    );

    @Query("SELECT new com.n1netails.n1netails.api.model.dto.TailTimestampAndResolvedTimestamp(t.timestamp, t.resolvedTimestamp) " +
           "FROM TailEntity t WHERE t.resolvedTimestamp IS NOT NULL AND t.organization.id IN :orgIds AND (:userId IS NULL OR t.user.id = :userId)")
    List<TailTimestampAndResolvedTimestamp> findTimestampsForMTTRCalculation(
            @Param("orgIds") Set<Long> orgIds,
            @Param("userId") Long userId
    );

    @Query("SELECT new com.n1netails.n1netails.api.model.dto.TailTimestampAndResolvedTimestamp(t.timestamp, t.resolvedTimestamp) " +
           "FROM TailEntity t WHERE t.resolvedTimestamp IS NOT NULL AND t.timestamp >= :daysAgo AND t.organization.id IN :orgIds AND (:userId IS NULL OR t.user.id = :userId)")
    List<TailTimestampAndResolvedTimestamp> findTimestampsForMTTRLast7Days(
            @Param("daysAgo") Instant daysAgo,
            @Param("orgIds") Set<Long> orgIds,
            @Param("userId") Long userId
    );

    @Query("SELECT t.timestamp FROM TailEntity t WHERE t.timestamp BETWEEN :start AND :end AND t.organization.id IN :orgIds AND (:userId IS NULL OR t.user.id = :userId) ORDER BY t.timestamp ASC")
    List<Instant> findTimestampsBetweenForHourly(
            @Param("start") Instant start,
            @Param("end") Instant end,
            @Param("orgIds") Set<Long> orgIds,
            @Param("userId") Long userId
    );

    @Query("SELECT new com.n1netails.n1netails.api.model.dto.TailLevelAndTimestamp(t.timestamp, t.level) " +
           "FROM TailEntity t WHERE t.timestamp BETWEEN :start AND :end AND t.organization.id IN :orgIds AND (:userId IS NULL OR t.user.id = :userId) ORDER BY t.timestamp ASC")
    List<TailLevelAndTimestamp> findLevelAndTimestampsBetweenForMonthlySummary(
            @Param("start") Instant start,
            @Param("end") Instant end,
            @Param("orgIds") Set<Long> orgIds,
            @Param("userId") Long userId
    );

    // Keep existing non-metric related methods
    long countByTimestampBetween(Instant startOfDay, Instant endOfDay); // Might be unused now or used by other services
    List<TailEntity> findByTimestampBetween(Instant startOfDay, Instant endOfDay); // Might be unused now or used by other services
    @Query("SELECT t.timestamp FROM TailEntity t WHERE t.timestamp BETWEEN :start AND :end ORDER BY t.timestamp ASC")
    List<Instant> findOnlyTimestampsBetween(@Param("start") Instant start, @Param("end") Instant end); // Might be unused
    @Query("SELECT new com.n1netails.n1netails.api.model.dto.TailLevelAndTimestamp(t.timestamp, t.level) " +
            "FROM TailEntity t WHERE t.timestamp BETWEEN :start AND :end ORDER BY t.timestamp ASC")
    List<TailLevelAndTimestamp> findOnlyLevelAndTimestampsBetween(@Param("start") Instant start, @Param("end") Instant end); // Might be unused

    List<TailEntity> findAllByStatusName(String statusName); // Might be unused
    long countByStatusName(String statusName); // Might be unused
    List<TailEntity> findAllByStatusNameNot(String statusName); // Might be unused
    long countByStatusNameNot(String statusName); // Might be unused
    @Query("SELECT new com.n1netails.n1netails.api.model.dto.TailTimestampAndResolvedTimestamp(t.timestamp, t.resolvedTimestamp) " +
            "FROM TailEntity t WHERE t.resolvedTimestamp IS NOT NULL")
    List<TailTimestampAndResolvedTimestamp> findOnlyTimestampAndResolvedTimestampIsNotNull(); // Might be unused
    @Query("SELECT new com.n1netails.n1netails.api.model.dto.TailTimestampAndResolvedTimestamp(t.timestamp, t.resolvedTimestamp) " +
            "FROM TailEntity t WHERE t.resolvedTimestamp IS NOT NULL AND t.timestamp >= :daysAgo")
    List<TailTimestampAndResolvedTimestamp> findAllByTimestampAfterAndResolvedTimestampIsNotNull(@Param("daysAgo") Instant daysAgo); // Might be unused
}

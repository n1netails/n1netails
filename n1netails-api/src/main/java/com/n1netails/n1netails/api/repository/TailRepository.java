package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.dto.TailLevelAndTimestamp;
import com.n1netails.n1netails.api.model.dto.TailSummary;
import com.n1netails.n1netails.api.model.dto.TailTimestampAndResolvedTimestamp;
import com.n1netails.n1netails.api.model.entity.TailEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TailRepository extends JpaRepository<TailEntity, Long> {

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

    long countByTimestampBetween(Instant startOfDay, Instant endOfDay);
    List<TailEntity> findByTimestampBetween(Instant startOfDay, Instant endOfDay);

    @Query("SELECT t.timestamp FROM TailEntity t WHERE t.timestamp BETWEEN :start AND :end ORDER BY t.timestamp ASC")
    List<Instant> findOnlyTimestampsBetween(
            @Param("start") Instant start,
            @Param("end") Instant end
    );

    @Query("SELECT new com.n1netails.n1netails.api.model.dto.TailLevelAndTimestamp(t.timestamp, t.level) " +
            "FROM TailEntity t WHERE t.timestamp BETWEEN :start AND :end ORDER BY t.timestamp ASC")
    List<TailLevelAndTimestamp> findOnlyLevelAndTimestampsBetween(
            @Param("start") Instant start,
            @Param("end") Instant end
    );

    List<TailEntity> findAllByStatusName(String statusName);
    long countByStatusName(String statusName);

    List<TailEntity> findAllByStatusNameNot(String statusName);
    long countByStatusNameNot(String statusName);

    @Query("SELECT new com.n1netails.n1netails.api.model.dto.TailTimestampAndResolvedTimestamp(t.timestamp, t.resolvedTimestamp) " +
            "FROM TailEntity t WHERE t.resolvedTimestamp IS NOT NULL")
    List<TailTimestampAndResolvedTimestamp> findOnlyTimestampAndResolvedTimestampIsNotNull();

    @Query("SELECT new com.n1netails.n1netails.api.model.dto.TailTimestampAndResolvedTimestamp(t.timestamp, t.resolvedTimestamp) " +
            "FROM TailEntity t WHERE t.resolvedTimestamp IS NOT NULL AND t.timestamp >= :daysAgo")
    List<TailTimestampAndResolvedTimestamp> findAllByTimestampAfterAndResolvedTimestampIsNotNull(@Param("daysAgo") Instant daysAgo);

    // For users in "n1netails" org (view own tails)
    @Query("""
        SELECT new com.n1netails.n1netails.api.model.dto.TailSummary(
            t.id, t.title, t.description, t.timestamp, t.resolvedTimestamp, t.assignedUserId,
            l.name, ty.name, s.name
        )
        FROM TailEntity t
        JOIN t.level l
        JOIN t.type ty
        JOIN t.status s
        WHERE t.assignedUserId = :assignedUserId
        AND s.name IN :statuses
        AND ty.name IN :types
        AND l.name IN :levels
        AND (t.title LIKE %:searchTerm% OR t.description LIKE %:searchTerm%)
        ORDER BY t.timestamp DESC
    """)
    Page<TailSummary> findByAssignedUserIdAndSearchTermAndFilters(
        @Param("assignedUserId") Long assignedUserId,
        @Param("searchTerm") String searchTerm,
        @Param("statuses") List<String> statuses,
        @Param("types") List<String> types,
        @Param("levels") List<String> levels,
        Pageable pageable
    );

    // For users in other orgs (view tails in their orgs)
    @Query("""
        SELECT new com.n1netails.n1netails.api.model.dto.TailSummary(
            t.id, t.title, t.description, t.timestamp, t.resolvedTimestamp, t.assignedUserId,
            l.name, ty.name, s.name
        )
        FROM TailEntity t
        JOIN t.level l
        JOIN t.type ty
        JOIN t.status s
        JOIN t.organization o
        WHERE o.id IN :organizationIds
        AND s.name IN :statuses
        AND ty.name IN :types
        AND l.name IN :levels
        AND (t.title LIKE %:searchTerm% OR t.description LIKE %:searchTerm%)
        ORDER BY t.timestamp DESC
    """)
    Page<TailSummary> findByOrganizationIdInAndSearchTermAndFilters(
        @Param("organizationIds") List<Long> organizationIds,
        @Param("searchTerm") String searchTerm,
        @Param("statuses") List<String> statuses,
        @Param("types") List<String> types,
        @Param("levels") List<String> levels,
        Pageable pageable
    );

    // Simplified versions for getTop9NewestTails
    @Query("""
        SELECT new com.n1netails.n1netails.api.model.dto.TailSummary(
            t.id, t.title, t.description, t.timestamp, t.resolvedTimestamp, t.assignedUserId,
            l.name, ty.name, s.name
        )
        FROM TailEntity t
        JOIN t.level l
        JOIN t.type ty
        JOIN t.status s
        WHERE t.assignedUserId = :assignedUserId AND t.resolvedTimestamp IS NULL
        ORDER BY t.timestamp DESC
    """)
    Page<TailSummary> findByAssignedUserIdAndResolvedTimestampIsNullOrderByTimestampDesc(
        @Param("assignedUserId") Long assignedUserId,
        Pageable pageable
    );

    @Query("""
        SELECT new com.n1netails.n1netails.api.model.dto.TailSummary(
            t.id, t.title, t.description, t.timestamp, t.resolvedTimestamp, t.assignedUserId,
            l.name, ty.name, s.name
        )
        FROM TailEntity t
        JOIN t.level l
        JOIN t.type ty
        JOIN t.status s
        JOIN t.organization o
        WHERE o.id IN :organizationIds AND t.resolvedTimestamp IS NULL
        ORDER BY t.timestamp DESC
    """)
    Page<TailSummary> findByOrganizationIdInAndResolvedTimestampIsNullOrderByTimestampDesc(
        @Param("organizationIds") List<Long> organizationIds,
        Pageable pageable
    );
}

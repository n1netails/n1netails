package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.dto.TailSummary;
import com.n1netails.n1netails.api.model.entity.TailBookmarkEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TailBookmarkRepository extends JpaRepository<TailBookmarkEntity, Long> {

    Optional<TailBookmarkEntity> findByUserIdAndTailId(Long userId, Long tailId);

    List<TailBookmarkEntity> findByUserId(Long userId);

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
        JOIN TailBookmarkEntity tb ON tb.tail = t
        WHERE s.name IN :statuses
        AND ty.name IN :types
        AND l.name IN :levels
        AND (t.title LIKE %:searchTerm% OR t.description LIKE %:searchTerm%)
        AND tb.user.id = :userId
        ORDER BY t.timestamp DESC
    """)
    Page<TailSummary> findAllBookmarksBySearchTermAndTailFilters(
            @Param("searchTerm") String searchTerm,
            @Param("statuses") List<String> statuses,
            @Param("types") List<String> types,
            @Param("levels") List<String> levels,
            @Param("userId") Long userId,
            Pageable pageable
    );

    boolean existsByUserIdAndTailId(Long userId, Long tailId);

    @Transactional
    void deleteByUserIdAndTailId(Long userId, Long tailId);
}

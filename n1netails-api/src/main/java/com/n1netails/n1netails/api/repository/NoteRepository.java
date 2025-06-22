package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.NoteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<NoteEntity, Long> {

    @Query("SELECT n FROM NoteEntity n " +
            "LEFT JOIN FETCH n.tail " +
            "LEFT JOIN FETCH n.user " +
            "LEFT JOIN FETCH n.organization " +
            "WHERE n.tail.id = :tailId " +
            "ORDER BY n.createdAt ASC")
    List<NoteEntity> findAllByTailIdOrderByCreatedAtAsc(@Param("tailId") Long tailId);

    List<NoteEntity> findTop9ByTailIdOrderByCreatedAtDesc(Long tailId);

    Page<NoteEntity> findAllByTailId(Long tailId, Pageable pageable);

    Optional<NoteEntity> findFirstByTailIdAndN1IsTrueOrderByCreatedAtDesc(Long tailId);
}

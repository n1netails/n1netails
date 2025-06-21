package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.NoteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<NoteEntity, Long> {

    List<NoteEntity> findAllByTailIdOrderByCreatedAtDesc(Long tailId);

    List<NoteEntity> findTop9ByTailIdOrderByCreatedAtDesc(Long tailId);

    Page<NoteEntity> findAllByTailId(Long tailId, Pageable pageable);

    Optional<NoteEntity> findFirstByTailIdAndN1IsTrueOrderByCreatedAtDesc(Long tailId);
}

package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<NoteEntity, Long> {
}

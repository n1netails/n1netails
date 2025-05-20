package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.TailStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TailStatusRepository extends JpaRepository<TailStatusEntity, Long> {

    Optional<TailStatusEntity> findTailStatusByName(String name);
}

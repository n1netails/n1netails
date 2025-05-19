package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.TailLevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TailLevelRepository extends JpaRepository<TailLevelEntity, Long> {

    Optional<TailLevelEntity> findTailLevelByName(String name);
}

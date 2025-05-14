package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.TailLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TailLevelRepository extends JpaRepository<TailLevel, Long> {

    Optional<TailLevel> findTailLevelByName(String name);
}

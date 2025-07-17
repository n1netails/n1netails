package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.TailLevelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TailLevelRepository extends PagingAndSortingRepository<TailLevelEntity, Long>, JpaRepository<TailLevelEntity, Long> {

    Optional<TailLevelEntity> findTailLevelByName(String name);

    Page<TailLevelEntity> findByNameContainingIgnoreCase(String searchTerm, Pageable pageable);
}

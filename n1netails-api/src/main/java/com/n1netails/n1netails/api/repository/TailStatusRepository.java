package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.TailStatusEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TailStatusRepository extends PagingAndSortingRepository<TailStatusEntity, Long>, JpaRepository<TailStatusEntity, Long> {

    Optional<TailStatusEntity> findTailStatusByName(String name);

    Page<TailStatusEntity> findByNameContainingIgnoreCase(String searchTerm, Pageable pageable);
}

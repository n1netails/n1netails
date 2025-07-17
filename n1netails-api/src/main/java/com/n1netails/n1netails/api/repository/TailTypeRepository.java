package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.TailTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TailTypeRepository extends PagingAndSortingRepository<TailTypeEntity, Long>, JpaRepository<TailTypeEntity, Long> {

    Optional<TailTypeEntity> findTailTypeByName(String name);

    Page<TailTypeEntity> findByNameContainingIgnoreCase(String searchTerm, Pageable pageable);
}

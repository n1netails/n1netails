package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.TailEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TailRepository extends JpaRepository<TailEntity, Long> {
    Page<TailEntity> findByAssignedUserId(String userId, Pageable pageable);
}

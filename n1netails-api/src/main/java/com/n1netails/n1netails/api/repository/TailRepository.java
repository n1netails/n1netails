package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.TailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TailRepository extends JpaRepository<TailEntity, Long> {
    List<TailEntity> findByAssignedUserId(String userId);
}

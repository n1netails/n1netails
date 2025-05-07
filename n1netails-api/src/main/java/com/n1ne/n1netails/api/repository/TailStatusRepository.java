package com.n1ne.n1netails.api.repository;

import com.n1ne.n1netails.api.model.entity.TailStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TailStatusRepository extends JpaRepository<TailStatus, Long> {

    Optional<TailStatus> findTailStatusByName(String name);
}

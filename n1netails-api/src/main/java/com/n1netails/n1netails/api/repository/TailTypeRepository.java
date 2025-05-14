package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.TailType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TailTypeRepository extends JpaRepository<TailType, Long> {

    Optional<TailType> findTailTypeByName(String name);
}

package com.n1ne.n1netails.api.repository;

import com.n1ne.n1netails.api.model.entity.TailVariable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TailVariableRepository extends JpaRepository<TailVariable, Long> {
}

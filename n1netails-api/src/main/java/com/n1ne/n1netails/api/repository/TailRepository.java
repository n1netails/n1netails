package com.n1ne.n1netails.api.repository;

import com.n1ne.n1netails.api.model.entity.Tail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TailRepository extends JpaRepository<Tail, Long> {
    List<Tail> findByAssignedUserId(String userId);
}

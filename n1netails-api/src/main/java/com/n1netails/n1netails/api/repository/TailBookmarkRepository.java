package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.TailBookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TailBookmarkRepository extends JpaRepository<TailBookmarkEntity, Long> {

    Optional<TailBookmarkEntity> findByUserIdAndTailId(Long userId, Long tailId);

    List<TailBookmarkEntity> findByUserId(Long userId);

    boolean existsByUserIdAndTailId(Long userId, Long tailId);

    @Transactional
    void deleteByUserIdAndTailId(Long userId, Long tailId);
}

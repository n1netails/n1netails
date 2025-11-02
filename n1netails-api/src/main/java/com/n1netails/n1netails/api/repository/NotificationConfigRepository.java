package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.NotificationConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationConfigRepository extends JpaRepository<NotificationConfigEntity, Long> {

    List<NotificationConfigEntity> findByTokenId(Long tokenId);
}

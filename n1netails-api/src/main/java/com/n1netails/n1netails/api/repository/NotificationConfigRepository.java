package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.NotificationConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationConfigRepository extends JpaRepository<NotificationConfig, Long> {

    List<NotificationConfig> findByTokenId(Long tokenId);
}

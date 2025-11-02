package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.UserNotificationPreferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNotificationPreferenceRepository extends JpaRepository<UserNotificationPreferenceEntity, Long> {

    List<UserNotificationPreferenceEntity> findByUserId(Long userId);
}

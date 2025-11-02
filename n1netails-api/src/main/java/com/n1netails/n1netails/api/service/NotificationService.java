package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.model.entity.NotificationConfigEntity;

import java.util.List;

public interface NotificationService {

    List<NotificationConfigEntity> getDecryptedConfigurations(Long tokenId);
    void saveConfigurations(Long tokenId, List<NotificationConfigEntity> configs);
    List<String> getUserNotificationPreferences(Long userId);
    void saveUserNotificationPreferences(Long userId, List<String> platforms);
}

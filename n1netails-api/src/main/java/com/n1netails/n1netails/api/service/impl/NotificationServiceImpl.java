package com.n1netails.n1netails.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1netails.n1netails.api.model.entity.NotificationConfigEntity;
import com.n1netails.n1netails.api.model.entity.UserNotificationPreferenceEntity;
import com.n1netails.n1netails.api.repository.NotificationConfigRepository;
import com.n1netails.n1netails.api.repository.UserNotificationPreferenceRepository;
import com.n1netails.n1netails.api.service.EncryptionService;
import com.n1netails.n1netails.api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("notificationService")
public class NotificationServiceImpl implements NotificationService {

    private final NotificationConfigRepository notificationConfigRepository;
    private final UserNotificationPreferenceRepository userNotificationPreferenceRepository;
    private final EncryptionService encryptionService;
    private final ObjectMapper objectMapper;

    public List<NotificationConfigEntity> getDecryptedConfigurations(Long tokenId) {
        return notificationConfigRepository.findByTokenId(tokenId).stream()
                .peek(config -> {
                    config.getDetails().forEach((key, value) -> {
                        try {
                            config.getDetails().put(key, encryptionService.decrypt(value));
                        } catch (Exception e) {
                            log.error("Error decrypting notification config details", e);
                            throw new RuntimeException("Error decrypting notification config details", e);
                        }
                    });
                })
                .collect(Collectors.toList());
    }

    public void saveConfigurations(Long tokenId, List<NotificationConfigEntity> configs) {
        notificationConfigRepository.findByTokenId(tokenId).forEach(notificationConfigRepository::delete);
        configs.forEach(config -> {
            try {
                config.setTokenId(tokenId);
                config.getDetails().forEach((key, value) -> {
                    try {
                        config.getDetails().put(key, encryptionService.encrypt(value));
                    } catch (Exception e) {
                        log.error("Error encrypting notification config details", e);
                        throw new RuntimeException("Error encrypting notification config details", e);
                    }
                });
                notificationConfigRepository.save(config);
            } catch (Exception e) {
                log.error("Error saving notification config", e);
                throw new RuntimeException("Error saving notification config", e);
            }
        });
    }

    public List<String> getUserNotificationPreferences(Long userId) {
        return userNotificationPreferenceRepository.findByUserId(userId).stream()
                .map(UserNotificationPreferenceEntity::getPlatform)
                .collect(Collectors.toList());
    }

    public void saveUserNotificationPreferences(Long userId, List<String> platforms) {
        userNotificationPreferenceRepository.findByUserId(userId).forEach(userNotificationPreferenceRepository::delete);
        platforms.forEach(platform -> {
            UserNotificationPreferenceEntity preference = new UserNotificationPreferenceEntity();
            preference.setUserId(userId);
            preference.setPlatform(platform);
            userNotificationPreferenceRepository.save(preference);
        });
    }
}

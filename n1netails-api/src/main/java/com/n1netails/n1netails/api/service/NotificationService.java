package com.n1netails.n1netails.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1netails.n1netails.api.model.entity.NotificationConfigEntity;
import com.n1netails.n1netails.api.model.entity.UserNotificationPreferenceEntity;
import com.n1netails.n1netails.api.repository.NotificationConfigRepository;
import com.n1netails.n1netails.api.repository.UserNotificationPreferenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationConfigRepository notificationConfigRepository;
    private final UserNotificationPreferenceRepository userNotificationPreferenceRepository;
    private final EncryptionService encryptionService;
    private final ObjectMapper objectMapper;

    public NotificationService(NotificationConfigRepository notificationConfigRepository,
                               UserNotificationPreferenceRepository userNotificationPreferenceRepository,
                               EncryptionService encryptionService,
                               ObjectMapper objectMapper) {
        this.notificationConfigRepository = notificationConfigRepository;
        this.userNotificationPreferenceRepository = userNotificationPreferenceRepository;
        this.encryptionService = encryptionService;
        this.objectMapper = objectMapper;
    }

    public List<NotificationConfigEntity> getDecryptedConfigurations(Long tokenId) {
        return notificationConfigRepository.findByTokenId(tokenId).stream()
                .peek(config -> {
                    config.getDetails().forEach((key, value) -> {
                        try {
                            config.getDetails().put(key, encryptionService.decrypt(value));
                        } catch (Exception e) {
                            logger.error("Error decrypting notification config details", e);
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
                        logger.error("Error encrypting notification config details", e);
                        throw new RuntimeException("Error encrypting notification config details", e);
                    }
                });
                notificationConfigRepository.save(config);
            } catch (Exception e) {
                logger.error("Error saving notification config", e);
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

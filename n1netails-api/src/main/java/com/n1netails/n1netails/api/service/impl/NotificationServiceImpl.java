package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.NotificationException;
import com.n1netails.n1netails.api.model.entity.NotificationConfigEntity;
import com.n1netails.n1netails.api.model.entity.UserNotificationPreferenceEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;
import com.n1netails.n1netails.api.repository.NotificationConfigRepository;
import com.n1netails.n1netails.api.repository.UserNotificationPreferenceRepository;
import com.n1netails.n1netails.api.service.EncryptionService;
import com.n1netails.n1netails.api.service.NotificationService;
import com.n1netails.n1netails.api.service.platform.NotificationPlatform;
import com.n1netails.n1netails.api.service.platform.NotificationPlatformRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.n1netails.n1netails.api.constant.PlatformConstant.*;

@Slf4j
@Service
@Qualifier("notificationService")
public class NotificationServiceImpl implements NotificationService {

    @Value("${n1netails.notifications.enabled}")
    private boolean notificationsEnabled;

    private final NotificationConfigRepository notificationConfigRepository;
    private final UserNotificationPreferenceRepository userNotificationPreferenceRepository;
    private final EncryptionService encryptionService;
    private final NotificationPlatformRegistry registry;

    public NotificationServiceImpl(
            NotificationConfigRepository notificationConfigRepository,
            UserNotificationPreferenceRepository userNotificationPreferenceRepository,
            EncryptionService encryptionService,
            NotificationPlatformRegistry registry
    ) {
        this.notificationConfigRepository = notificationConfigRepository;
        this.userNotificationPreferenceRepository = userNotificationPreferenceRepository;
        this.encryptionService = encryptionService;
        this.registry = registry;
    }

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

    @Override
    public void sendNotificationAlert(UsersEntity usersEntity, KudaTailRequest request, Long tokenId) throws Exception {
        if (!notificationsEnabled) return;

        Set<String> userPlatforms = userNotificationPreferenceRepository.findByUserId(usersEntity.getId())
                .stream().map(UserNotificationPreferenceEntity::getPlatform)
                .collect(Collectors.toSet());

        Map<String, Boolean> platformEnabledFlags = registry.getPlatformEnabledFlags();
        List<NotificationConfigEntity> configs = this.getDecryptedConfigurations(tokenId);

        for (NotificationConfigEntity config: configs) {
            String platform = config.getPlatform();
            if (!userPlatforms.contains(platform) || !platformEnabledFlags.getOrDefault(platform, false)) continue;
            NotificationPlatform handler = registry.get(platform);

            if (handler == null) {
                throw new NotificationException("Unsupported platform: " + platform);
            }
            handler.send(request, config);
        }
    }
}

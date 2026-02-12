package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.NotificationException;
import com.n1netails.n1netails.api.model.entity.NotificationConfigEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;

import java.util.List;

/**
 * Service responsible for managing user notifications and sending alerts.
 *
 * <p>
 *     Manages saving and loading notification settings and user preferences,
 *     and sends alerts to supported platforms
 * </p>
 */
public interface NotificationService {


    /**
     * Retrieves all notification configurations for a given token and decrypts
     * the configuration details.
     *
     * <p>
     * If no configurations exist for the token, returns an empty list.
     * <strong>Note:</strong> Decryption of configuration details may fail.
     * If a decryption error occurs, a {@link RuntimeException} is thrown.
     * </p>
     *
     * @param tokenId the identifier of the N1ne token
     * @return a list of decrypted notification configurations; never {@code null}
     * @throws RuntimeException if decryption of any configuration detail fails
     */
    List<NotificationConfigEntity> getDecryptedConfigurations(Long tokenId);

    /**
     * Saves the given notification configurations for the specified token.
     *
     * <p>
     * Existing configurations for the token are deleted before saving the new list.
     * Configuration details are encrypted before storage.
     * <strong>Note:</strong> Encryption may fail; in that case, a {@link RuntimeException} is thrown.
     * </p>
     *
     * @param tokenId the identifier of the N1ne token
     * @param configs the list of notification configurations to save
     * @throws RuntimeException if encryption or saving fails
     */
    void saveConfigurations(Long tokenId, List<NotificationConfigEntity> configs);

    /**
     * Retrieves the notification platforms preferred by a user.
     *
     * <p>
     * If the user has no preferences, returns an empty list.
     * </p>
     *
     * @param userId the identifier of the user
     * @return list of platform names; never {@code null}
     */
    List<String> getUserNotificationPreferences(Long userId);


    /**
     * Saves the notification platform preferences for a user.
     *
     * <p>
     * Existing preferences for the user are deleted before saving the new list.
     * </p>
     *
     * @param userId    the identifier of the user
     * @param platforms list of platform names to save
     * @throws RuntimeException if saving fails
     */
    void saveUserNotificationPreferences(Long userId, List<String> platforms);

    /**
     * Sends a notification alert to the user's preferred platforms.
     *
     * <p>
     * Only platforms that are both enabled for the user and globally enabled
     * are sent. If notifications are disabled globally, this method does nothing.
     * </p>
     *
     * @param usersEntity the target user
     * @param request     the tail request that triggered the alert
     * @param tokenId     the identifier of the N1ne token to retrieve configurations
     * @throws NotificationException if a platform is unsupported
     * @throws Exception             if encryption/decryption or sending fails
     */
    void sendNotificationAlert(UsersEntity usersEntity, KudaTailRequest request, Long tokenId) throws Exception;
}

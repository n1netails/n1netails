package com.n1netails.n1netails.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1netails.n1netails.api.model.entity.NotificationConfigEntity;
import com.n1netails.n1netails.api.model.entity.UserNotificationPreferenceEntity;
import com.n1netails.n1netails.api.repository.NotificationConfigRepository;
import com.n1netails.n1netails.api.repository.UserNotificationPreferenceRepository;
import com.n1netails.n1netails.api.service.EncryptionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

    @Mock
    private NotificationConfigRepository notificationConfigRepository;

    @Mock
    private UserNotificationPreferenceRepository userNotificationPreferenceRepository;

    @Mock
    private EncryptionService encryptionService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    public void testGetDecryptedConfigurations() throws Exception {
        NotificationConfigEntity config = new NotificationConfigEntity();
        config.setDetails(new java.util.HashMap<>() {{
            put("key", "encryptedDetails");
        }});
        when(notificationConfigRepository.findByTokenId(1L)).thenReturn(Arrays.asList(config));
        when(encryptionService.decrypt("encryptedDetails")).thenReturn("decryptedDetails");

        List<NotificationConfigEntity> result = notificationService.getDecryptedConfigurations(1L);

        assertEquals(1, result.size());
        assertEquals("decryptedDetails", result.get(0).getDetails().get("key"));
    }

    @Test
    public void testSaveConfigurations() throws Exception {
        NotificationConfigEntity config = new NotificationConfigEntity();
        config.setDetails(new java.util.HashMap<>() {{
            put("key", "details");
        }});
        when(notificationConfigRepository.findByTokenId(1L)).thenReturn(Arrays.asList(new NotificationConfigEntity()));
        when(encryptionService.encrypt("details")).thenReturn("encryptedDetails");

        notificationService.saveConfigurations(1L, Arrays.asList(config));

        verify(notificationConfigRepository, times(1)).delete(any());
        verify(notificationConfigRepository, times(1)).save(any());
    }

    @Test
    public void testGetUserNotificationPreferences() {
        UserNotificationPreferenceEntity preference = new UserNotificationPreferenceEntity();
        preference.setPlatform("email");
        when(userNotificationPreferenceRepository.findByUserId(1L)).thenReturn(Arrays.asList(preference));

        List<String> result = notificationService.getUserNotificationPreferences(1L);

        assertEquals(1, result.size());
        assertEquals("email", result.get(0));
    }

    @Test
    public void testSaveUserNotificationPreferences() throws Exception {
        when(userNotificationPreferenceRepository.findByUserId(1L)).thenReturn(Arrays.asList(new UserNotificationPreferenceEntity()));
        notificationService.saveUserNotificationPreferences(1L, Arrays.asList("email"));

        verify(userNotificationPreferenceRepository, times(1)).delete(any());
        verify(userNotificationPreferenceRepository, times(1)).save(any());
    }
}

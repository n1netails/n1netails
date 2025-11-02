package com.n1netails.n1netails.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1netails.n1netails.api.model.NotificationConfig;
import com.n1netails.n1netails.api.model.UserNotificationPreference;
import com.n1netails.n1netails.api.repository.NotificationConfigRepository;
import com.n1netails.n1netails.api.repository.UserNotificationPreferenceRepository;
import com.n1netails.n1netails.api.service.EncryptionService;
import com.n1netails.n1netails.api.service.NotificationService;
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
public class NotificationServiceTest {

    @Mock
    private NotificationConfigRepository notificationConfigRepository;

    @Mock
    private UserNotificationPreferenceRepository userNotificationPreferenceRepository;

    @Mock
    private EncryptionService encryptionService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    public void testGetDecryptedConfigurations() throws Exception {
        NotificationConfig config = new NotificationConfig();
        config.setDetails(new java.util.HashMap<>() {{
            put("key", "encryptedDetails");
        }});
        when(notificationConfigRepository.findByTokenId(1L)).thenReturn(Arrays.asList(config));
        when(encryptionService.decrypt("encryptedDetails")).thenReturn("decryptedDetails");

        List<NotificationConfig> result = notificationService.getDecryptedConfigurations(1L);

        assertEquals(1, result.size());
        assertEquals("decryptedDetails", result.get(0).getDetails().get("key"));
    }

    @Test
    public void testSaveConfigurations() throws Exception {
        NotificationConfig config = new NotificationConfig();
        config.setDetails(new java.util.HashMap<>() {{
            put("key", "details");
        }});
        when(notificationConfigRepository.findByTokenId(1L)).thenReturn(Arrays.asList(new NotificationConfig()));
        when(encryptionService.encrypt("details")).thenReturn("encryptedDetails");

        notificationService.saveConfigurations(1L, Arrays.asList(config));

        verify(notificationConfigRepository, times(1)).delete(any());
        verify(notificationConfigRepository, times(1)).save(any());
    }

    @Test
    public void testGetUserNotificationPreferences() {
        UserNotificationPreference preference = new UserNotificationPreference();
        preference.setPlatform("email");
        when(userNotificationPreferenceRepository.findByUserId(1L)).thenReturn(Arrays.asList(preference));

        List<String> result = notificationService.getUserNotificationPreferences(1L);

        assertEquals(1, result.size());
        assertEquals("email", result.get(0));
    }

    @Test
    public void testSaveUserNotificationPreferences() {
        when(userNotificationPreferenceRepository.findByUserId(1L)).thenReturn(Arrays.asList(new UserNotificationPreference()));
        notificationService.saveUserNotificationPreferences(1L, Arrays.asList("email"));

        verify(userNotificationPreferenceRepository, times(1)).delete(any());
        verify(userNotificationPreferenceRepository, times(1)).save(any());
    }
}

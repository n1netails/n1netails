package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.model.entity.NotificationConfigEntity;
import com.n1netails.n1netails.api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/ninetails/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{tokenId}")
    public ResponseEntity<List<NotificationConfigEntity>> getConfigurations(@PathVariable Long tokenId) {
        return ResponseEntity.ok(notificationService.getDecryptedConfigurations(tokenId));
    }

    @PostMapping("/{tokenId}")
    public ResponseEntity<Void> saveConfigurations(@PathVariable Long tokenId, @RequestBody List<NotificationConfigEntity> configs) {
        notificationService.saveConfigurations(tokenId, configs);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}/preferences")
    public ResponseEntity<List<String>> getUserPreferences(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUserNotificationPreferences(userId));
    }

    @PostMapping("/user/{userId}/preferences")
    public ResponseEntity<Void> saveUserPreferences(@PathVariable Long userId, @RequestBody List<String> platforms) {
        notificationService.saveUserNotificationPreferences(userId, platforms);
        return ResponseEntity.ok().build();
    }
}

package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.NotificationConfigEntity;
import com.n1netails.n1netails.api.model.response.N1neTokenResponse;
import com.n1netails.n1netails.api.service.AuthorizationService;
import com.n1netails.n1netails.api.service.N1neTokenService;
import com.n1netails.n1netails.api.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Notification Controller", description = "Operations related to Notifications")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/ninetails/notifications")
@ConditionalOnProperty(
        prefix = "n1netails.notifications",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class NotificationController {

    private final NotificationService notificationService;
    private final N1neTokenService n1neTokenService;
    private final AuthorizationService authorizationService;

    @Operation(summary = "Get token notification configurations", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved token notification configurations"),
            @ApiResponse(responseCode = "404", description = "Token notification configurations not found")
    })
    @GetMapping("/save/config/{tokenId}")
    public ResponseEntity<List<NotificationConfigEntity>> getConfigurations(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long tokenId
    ) throws UserNotFoundException, AccessDeniedException {
        N1neTokenResponse n1neToken = this.n1neTokenService.getById(tokenId);
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        if (!authorizationService.isOwnerOrOrganizationAdmin(currentUser, n1neToken.getUserId(), n1neToken.getOrganizationId())) {
            throw new AccessDeniedException("Get token request access denied.");
        }
        return ResponseEntity.ok(notificationService.getDecryptedConfigurations(tokenId));
    }

    @Operation(summary = "Save token notification configurations", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully saved token notification configurations"),
            @ApiResponse(responseCode = "404", description = "Token notification configurations not found")
    })
    @PostMapping("/save/config/{tokenId}")
    public ResponseEntity<Void> saveConfigurations(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long tokenId,
            @RequestBody List<NotificationConfigEntity> configs
    ) throws AccessDeniedException, UserNotFoundException {
        N1neTokenResponse n1neToken = this.n1neTokenService.getById(tokenId);
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        if (!authorizationService.isOwnerOrOrganizationAdmin(currentUser, n1neToken.getUserId(), n1neToken.getOrganizationId())) {
            throw new AccessDeniedException("Get token request access denied.");
        }
        notificationService.saveConfigurations(tokenId, configs);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get user notification preferences", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user notification preferences"),
            @ApiResponse(responseCode = "404", description = "User notification preferences not found")
    })
    @GetMapping("/user/{userId}/preferences")
    public ResponseEntity<List<String>> getUserPreferences(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long userId
    ) throws UserNotFoundException, AccessDeniedException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        if (!authorizationService.isSelf(currentUser, userId)) {
            throw new AccessDeniedException("Get user preferences access denied.");
        }
        return ResponseEntity.ok(notificationService.getUserNotificationPreferences(userId));
    }

    @Operation(summary = "Save user notification preferences", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully saved user notification preferences"),
            @ApiResponse(responseCode = "404", description = "User notification preferences not found")
    })
    @PostMapping("/user/{userId}/preferences")
    public ResponseEntity<Void> saveUserPreferences(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long userId,
            @RequestBody List<String> platforms
    ) throws UserNotFoundException, AccessDeniedException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        if (!authorizationService.isSelf(currentUser, userId)) {
            throw new AccessDeniedException("Save user preferences access denied.");
        }
        notificationService.saveUserNotificationPreferences(userId, platforms);
        return ResponseEntity.ok().build();
    }
}

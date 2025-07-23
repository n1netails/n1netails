package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.N1neTokenNotFoundException;
import com.n1netails.n1netails.api.exception.type.OrganizationNotFoundException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;
import com.n1netails.n1netails.api.service.AlertService;
import com.n1netails.n1netails.api.service.AuthorizationService;
import com.n1netails.n1netails.api.service.N1neTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

import static com.n1netails.n1netails.api.constant.ControllerConstant.APPLICATION_JSON;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Alert Controller", description = "Operations related to N1ne Alerts (Utilized by Kuda)")
@RestController
@RequestMapping(path = {"/ninetails/alert"}, produces = APPLICATION_JSON)
public class AlertController {

    private static final int TITLE_MAX_LENGTH = 255;
    private static final int DESCRIPTION_MAX_LENGTH = 255;
    private static final int METADATA_KEY_LENGTH = 255;
    private static final int METADATA_VALUE_LENGTH = 255;

    private final AlertService alertService;
    private final N1neTokenService n1neTokenService;
    private final AuthorizationService authorizationService;

    @Operation(summary = "Create a new alert", responses = {
            @ApiResponse(responseCode = "204", description = "Alert created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping(consumes = APPLICATION_JSON)
    public ResponseEntity<Void> create(
            @RequestHeader("N1ne-Token") String n1neToken,
            @RequestBody KudaTailRequest request
    ) throws N1neTokenNotFoundException {
        log.info("=====================");
        log.info("RECEIVED KUDA REQUEST");
        sanitizeRequestData(request);
        boolean tokenValid = this.n1neTokenService.validateToken(n1neToken);
        if (tokenValid) {
            this.n1neTokenService.setLastUsedAt(n1neToken);
            alertService.createTail(n1neToken, request);
        }
        else {
            // Log internally, but don’t reveal to client
            log.warn("Unauthorized access attempt with token: {}...", n1neToken.substring(0, 5));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Create a new manual alert", responses = {
            @ApiResponse(responseCode = "204", description = "Manual Alert created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping(value = "/manual/{userId}/organization/{organizationId}", consumes = APPLICATION_JSON)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> createManual(
            @RequestHeader(AUTHORIZATION) String authorizationHeader,
            @PathVariable Long userId,
            @PathVariable Long organizationId,
            @RequestBody KudaTailRequest request
    ) throws UserNotFoundException, AccessDeniedException, OrganizationNotFoundException {
        log.info("=====================");
        log.info("RECEIVED MANUAL REQUEST");

        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        if (authorizationService.isSelf(currentUser, userId)
            && authorizationService.belongsToOrganization(currentUser, organizationId)
        ) {
            log.info("Manually adding tail alert");
            sanitizeRequestData(request);
            alertService.createManualTail(organizationId, currentUser.getUser(), request);
        } else {
            throw new AccessDeniedException("Create manual tail alert request access denied.");
        }
        return ResponseEntity.noContent().build();
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.isBlank() || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }

    private void sanitizeRequestData(KudaTailRequest request) {
        // substring title and description to meet db requirements
        request.setTitle(truncate(request.getTitle(), TITLE_MAX_LENGTH));
        request.setDescription(truncate(request.getDescription(), DESCRIPTION_MAX_LENGTH));

        // substring metadata to meet db requirements
        Map<String, String> metadata = new HashMap<>();
        request.getMetadata().forEach((k, v) -> {
            k = truncate(k, METADATA_KEY_LENGTH);
            v = truncate(v, METADATA_VALUE_LENGTH);
            metadata.put(k, v);
        });
        request.setMetadata(metadata);
    }
}

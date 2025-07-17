package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.N1neTokenNotFoundException;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;
import com.n1netails.n1netails.api.service.AlertService;
import com.n1netails.n1netails.api.service.N1neTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.n1netails.n1netails.api.constant.ControllerConstant.APPLICATION_JSON;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Alert Controller", description = "Operations related to N1ne Alerts (Utilized by Kuda)")
@RestController
@RequestMapping(path = {"/ninetails/alert"}, produces = APPLICATION_JSON)
public class AlertController {

    private static final int TITLE_MAX_LENGTH = 255;
    private static final int DESCRIPTION_MAX_LENGTH = 255;

    private final AlertService alertService;
    private final N1neTokenService n1neTokenService;

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

        // substring title and description to meet db requirements
        request.setTitle(truncate(request.getTitle(), TITLE_MAX_LENGTH));
        request.setDescription(truncate(request.getDescription(), DESCRIPTION_MAX_LENGTH));

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

    private String truncate(String value, int maxLength) {
        if (value == null || value.isBlank() || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }
}

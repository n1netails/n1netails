package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.dto.passkey.*;
import com.n1netails.n1netails.api.service.AuthorizationService;
import com.n1netails.n1netails.api.service.impl.PasskeyServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.nio.file.AccessDeniedException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Passkey Controller", description = "Operations related to Passkey (WebAuthn) authentication")
@RestController
@RequestMapping(path = {"/ninetails/auth/passkey"}, produces = APPLICATION_JSON_VALUE)
public class PasskeyController {

    private final PasskeyServiceImpl passkeyService;
    private final AuthorizationService authorizationService;

    @Operation(summary = "Start Passkey Registration", description = "Initiates the passkey registration process for a user.")
    @PostMapping(value = "/register/start", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<PasskeyRegistrationStartResponseDto> startRegistration(
            @RequestHeader(AUTHORIZATION) String authorizationHeader,
            @RequestBody PasskeyRegistrationStartRequestDto request
    ) throws UserNotFoundException, AccessDeniedException {

        UserPrincipal userPrincipal = authorizationService.getCurrentUserPrincipal(authorizationHeader);

        if (userPrincipal.getUser().getEmail().equals(request.getEmail())) {
            try {
                log.info("User {} starting passkey registration for their own account.", userPrincipal.getUsername());
                PasskeyRegistrationStartResponseDto response = passkeyService.startRegistration(request);
                return ResponseEntity.ok(response);
            } catch (UserNotFoundException e) {
                log.warn("User {} not found during start passkey registration. Details: {}", userPrincipal.getUsername(), e.getMessage());
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            } catch (Exception e) {
                log.error("Error starting passkey registration for user {}. Error: {}", userPrincipal.getUsername(), e.getMessage(), e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error starting passkey registration", e);
            }
        } else {
            log.warn("User {} attempted to register a passkey for a different target user account via /register/start endpoint. Denied.", userPrincipal.getUsername());
            throw new AccessDeniedException("You can only register passkeys for your own account using this endpoint.");
        }
    }

    @Operation(summary = "Finish Passkey Registration", description = "Completes the passkey registration process.")
    @PostMapping(value = "/register/finish", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<PasskeyApiResponseDto> finishRegistration(@RequestBody PasskeyRegistrationFinishRequestDto request) {
        try {
            log.info("Received request to finish passkey registration with flowId: {}", request.getFlowId());
            boolean success = passkeyService.finishRegistration(request);
            if (success) {
                return ResponseEntity.ok(new PasskeyApiResponseDto(true, "Passkey registration successful."));
            } else {
                return ResponseEntity.badRequest().body(new PasskeyApiResponseDto(false, "Passkey registration failed."));
            }
        } catch (UserNotFoundException e) {
            log.warn("User not found during finish passkey registration for flowId: {}. Details: {}", request.getFlowId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
        catch (Exception e) {
            log.error("Error finishing passkey registration with flowId: {}. Error: {}", request.getFlowId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PasskeyApiResponseDto(false, "Error finishing passkey registration: " + e.getMessage()));
        }
    }

    @Operation(summary = "Start Passkey Authentication", description = "Initiates the passkey authentication process.")
    @PostMapping(value = "/login/start", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<PasskeyAuthenticationStartResponseDto> startAuthentication(@RequestBody(required = false) PasskeyAuthenticationStartRequestDto request) {
        // Request can be null or empty for discoverable credentials (passkeys)
        PasskeyAuthenticationStartRequestDto actualRequest = (request == null) ? new PasskeyAuthenticationStartRequestDto() : request;

        String logIdentifier;
        if (actualRequest.getEmail() != null && !actualRequest.getEmail().isBlank()) {
            logIdentifier = "user (email provided for hint)";
        } else {
            logIdentifier = "discoverable credential user";
        }

        try {
            log.info("Received request to start passkey authentication for {}.", logIdentifier);
            PasskeyAuthenticationStartResponseDto response = passkeyService.startAuthentication(actualRequest);
            log.info("Returning start passkey authentication response for {}.", logIdentifier);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error starting passkey authentication for {}. Error: {}", logIdentifier, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error starting passkey authentication. Passkey might not exist for provided user email. You have to register an account and create a passkey through the edit profile page.", e);
        }
    }

    @Operation(summary = "Finish Passkey Authentication", description = "Completes the passkey authentication process and returns a JWT if successful.")
    @PostMapping(value = "/login/finish", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<PasskeyAuthenticationResponseDto> finishAuthentication(@RequestBody PasskeyAuthenticationFinishRequestDto request) {
        try {
            log.info("Received request to finish passkey authentication with flowId: {}", request.getFlowId());
            PasskeyAuthenticationResponseDto response = passkeyService.finishAuthentication(request);
            if (response.isSuccess()) {
                log.info("Successfully finished passkey authentication for flowId: {}", request.getFlowId());
                return ResponseEntity.ok(response);
            } else {
                log.warn("Passkey authentication failed for flowId: {}. Reason: {}", request.getFlowId(), response.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            log.error("Error finishing passkey authentication with flowId: {}. Error: {}", request.getFlowId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PasskeyAuthenticationResponseDto(false, "Error finishing passkey authentication: " + e.getMessage()));
        }
    }
}

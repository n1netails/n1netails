package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.dto.passkey.*;
import com.n1netails.n1netails.api.service.PasskeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import static com.n1netails.n1netails.api.constant.ControllerConstant.APPLICATION_JSON;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Passkey Controller", description = "Operations related to Passkey (WebAuthn) authentication")
@RestController
@RequestMapping(path = {"/ninetails/auth/passkey"}, produces = APPLICATION_JSON)
public class PasskeyController {

    private final PasskeyService passkeyService;

    @Operation(summary = "Start Passkey Registration", description = "Initiates the passkey registration process for a user.")
    @PostMapping(value = "/register/start", consumes = APPLICATION_JSON)
    public ResponseEntity<PasskeyRegistrationStartResponseDto> startRegistration(@RequestBody PasskeyRegistrationStartRequestDto request) {
        try {
            log.info("Received request to start passkey registration for email: {}", request.getEmail());
            PasskeyRegistrationStartResponseDto response = passkeyService.startRegistration(request);
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            log.warn("User not found during start passkey registration: {}", request.getEmail(), e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error starting passkey registration for email: {}", request.getEmail(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error starting passkey registration", e);
        }
    }

    @Operation(summary = "Finish Passkey Registration", description = "Completes the passkey registration process.")
    @PostMapping(value = "/register/finish", consumes = APPLICATION_JSON)
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
             log.warn("User not found during finish passkey registration for flowId: {}", request.getFlowId(), e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
        catch (Exception e) {
            log.error("Error finishing passkey registration with flowId: {}", request.getFlowId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PasskeyApiResponseDto(false, "Error finishing passkey registration: " + e.getMessage()));
        }
    }

    @Operation(summary = "Start Passkey Authentication", description = "Initiates the passkey authentication process.")
    @PostMapping(value = "/login/start", consumes = APPLICATION_JSON)
    public ResponseEntity<PasskeyAuthenticationStartResponseDto> startAuthentication(@RequestBody(required = false) PasskeyAuthenticationStartRequestDto request) {
        // Request can be null or empty for discoverable credentials (passkeys)
        PasskeyAuthenticationStartRequestDto actualRequest = (request == null) ? new PasskeyAuthenticationStartRequestDto() : request;
        try {
            log.info("Received request to start passkey authentication for email: {}", actualRequest.getEmail());
            PasskeyAuthenticationStartResponseDto response = passkeyService.startAuthentication(actualRequest);
            log.info("RETURNING START PASSKEY AUTHENTICATION RESPONSE");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error starting passkey authentication for email: {}", actualRequest.getEmail(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error starting passkey authentication", e);
        }
    }

    @Operation(summary = "Finish Passkey Authentication", description = "Completes the passkey authentication process and returns a JWT if successful.")
    @PostMapping(value = "/login/finish", consumes = APPLICATION_JSON)
    public ResponseEntity<PasskeyAuthenticationResponseDto> finishAuthentication(@RequestBody PasskeyAuthenticationFinishRequestDto request) {
        log.info("FINISH PASSKEY AUTHENTICATION");
        try {
            log.info("Received request to finish passkey authentication with flowId: {}", request.getFlowId());
            PasskeyAuthenticationResponseDto response = passkeyService.finishAuthentication(request);
            if (response.isSuccess()) {
                log.info("RETURNING FINISH PASSKEY AUTHENTICATION RESPONSE");
                return ResponseEntity.ok(response);
            } else {
                // Consider if specific errors from service layer should map to different HTTP statuses
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            log.error("Error finishing passkey authentication with flowId: {}", request.getFlowId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PasskeyAuthenticationResponseDto(false, "Error finishing passkey authentication: " + e.getMessage()));
        }
    }
}

package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.model.request.passkey.PasskeyLoginFinishRequest;
import com.n1netails.n1netails.api.model.request.passkey.PasskeyLoginStartRequest;
import com.n1netails.n1netails.api.model.request.passkey.PasskeyRegistrationFinishRequest;
import com.n1netails.n1netails.api.model.request.passkey.PasskeyRegistrationStartRequest;
import com.n1netails.n1netails.api.model.response.passkey.PasskeyAuthenticationResponse;
import com.n1netails.n1netails.api.model.response.passkey.PasskeyLoginStartResponse;
import com.n1netails.n1netails.api.model.response.passkey.PasskeyRegistrationStartResponse;
import com.n1netails.n1netails.api.service.PasskeyService; // Uncommented
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.n1netails.n1netails.api.constant.ControllerConstant.API_PASSKEY_V1_MAPPING;

@Slf4j
@RestController
@RequestMapping(API_PASSKEY_V1_MAPPING)
@RequiredArgsConstructor
// @CrossOrigin(origins = "*", maxAge = 3600) // Consider if needed, or use global config
public class PasskeyController {

    private final PasskeyService passkeyService; // Uncommented and enabled

    @PostMapping("/register/start")
    public ResponseEntity<PasskeyRegistrationStartResponse> startRegistration(@Valid @RequestBody PasskeyRegistrationStartRequest request) {
        log.info("Received passkey registration start request for username: {}", request.getUsername());
        PasskeyRegistrationStartResponse response = passkeyService.startRegistration(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/finish")
    public ResponseEntity<Void> finishRegistration(@Valid @RequestBody PasskeyRegistrationFinishRequest request) {
        log.info("Received passkey registration finish request with ID: {}", request.getRegistrationId());
        passkeyService.finishRegistration(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login/start")
    public ResponseEntity<PasskeyLoginStartResponse> startLogin(@RequestBody(required = false) PasskeyLoginStartRequest request) {
        // Username might be null for discoverable credentials
        log.info("Received passkey login start request for username: {}", request != null ? request.getUsername() : "N/A (discoverable)");
        PasskeyLoginStartResponse response = passkeyService.startLogin(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/finish")
    public ResponseEntity<PasskeyAuthenticationResponse> finishLogin(@Valid @RequestBody PasskeyLoginFinishRequest request) {
        log.info("Received passkey login finish request with ID: {}", request.getAssertionId());
        PasskeyAuthenticationResponse response = passkeyService.finishLogin(request);
        // Similar to UserController, we might want to return the token in a header.
        // However, PasskeyAuthenticationResponse already includes the token in its body.
        // For consistency, let's also add it to the header.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Jwt-Token", response.getToken());
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
}

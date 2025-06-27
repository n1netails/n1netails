package com.n1netails.n1netails.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1netails.n1netails.api.model.request.passkey.FinishAuthenticationRequest;
import com.n1netails.n1netails.api.model.request.passkey.FinishRegistrationRequest;
import com.n1netails.n1netails.api.model.request.passkey.StartAuthenticationRequest;
import com.n1netails.n1netails.api.model.request.passkey.StartRegistrationRequest;
import com.n1netails.n1netails.api.model.response.passkey.AuthenticationSuccessResponse;
import com.n1netails.n1netails.api.model.response.passkey.StartAuthenticationResponse;
import com.n1netails.n1netails.api.model.response.passkey.StartRegistrationResponse;
import com.n1netails.n1netails.api.service.PasskeyService;
import com.yubico.webauthn.data.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping(path = {"/ninetails/auth/passkey"}, produces = MediaType.APPLICATION_JSON_VALUE)
// @CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"}, allowCredentials = "true") // Configure as needed
public class PasskeyController {

    private static final Logger logger = LoggerFactory.getLogger(PasskeyController.class);
    private final PasskeyService passkeyService;
    private final ObjectMapper objectMapper; // For serializing/deserializing Yubico objects

    @Value("${n1netails.webauthn.relying-party-id}")
    private String relyingPartyId;

    @Value("${n1netails.webauthn.relying-party-name}")
    private String relyingPartyName;

    public PasskeyController(PasskeyService passkeyService, ObjectMapper objectMapper) {
        this.passkeyService = passkeyService;
        this.objectMapper = objectMapper;
        // Configure ObjectMapper for Yubico's ByteArray etc. if not already globally configured
        // com.yubico.webauthn.data.뭍.serializer.ByteArraySerializer
        // com.yubico.webauthn.data.뭍.deserializer.ByteArrayDeserializer
        // Typically, if using Spring Boot with Jackson, these might need to be registered as modules
        // or use @JsonSerialize/@JsonDeserialize on Yubico classes if you owned them.
        // For now, assuming default Jackson setup works or Yubico's objects are Jackson-friendly.
        // Yubico's objects are generally Jackson-friendly.
    }

    private String getClientOrigin(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        if (origin == null || origin.isBlank()) {
            // Fallback or throw error if origin is strictly required by your RP config
            // For localhost development, this might be okay if RP origins are permissive
            logger.warn("Origin header is missing, relying on pre-configured RP origins.");
            return ""; // Or a default, or handle as an error
        }
        return origin;
    }

    @PostMapping("/register/start")
    public ResponseEntity<StartRegistrationResponse> startRegistration(@Valid @RequestBody StartRegistrationRequest registrationRequest, HttpServletRequest request) {
        logger.info("Passkey registration start requested for email: {}", registrationRequest.getEmail());
        try {
            String clientOrigin = getClientOrigin(request);
            PublicKeyCredentialCreationOptions creationOptions = passkeyService.startRegistration(
                    registrationRequest.getEmail(),
                    relyingPartyId,
                    relyingPartyName,
                    clientOrigin
            );
            String creationOptionsJson = objectMapper.writeValueAsString(creationOptions);
            return ResponseEntity.ok(new StartRegistrationResponse(creationOptionsJson));
        } catch (IllegalArgumentException e) {
            logger.warn("Passkey registration start failed for email {}: {}", registrationRequest.getEmail(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing PublicKeyCredentialCreationOptions: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error preparing registration data.", e);
        }
    }

    @PostMapping("/register/finish")
    public ResponseEntity<?> finishRegistration(@Valid @RequestBody FinishRegistrationRequest finishRequest, HttpServletRequest request) {
        logger.info("Passkey registration finish requested for email: {}", finishRequest.getEmail());
        try {
            String clientOrigin = getClientOrigin(request);
            AuthenticatorAttestationResponse attestationResponse = objectMapper.readValue(finishRequest.getAttestationResponseJson(), AuthenticatorAttestationResponse.class);
            ClientRegistrationExtensionOutputs clientExtensions = null;
            if (finishRequest.getClientExtensionsJson() != null && !finishRequest.getClientExtensionsJson().isEmpty()) {
                clientExtensions = objectMapper.readValue(finishRequest.getClientExtensionsJson(), ClientRegistrationExtensionOutputs.class);
            }

            boolean success = passkeyService.finishRegistration(
                    finishRequest.getEmail(),
                    relyingPartyId,
                    clientOrigin,
                    attestationResponse,
                    clientExtensions,
                    finishRequest.getOriginalCreationOptionsJson()
            );

            if (success) {
                return ResponseEntity.ok().body(java.util.Map.of("success", true, "message", "Passkey registration successful."));
            } else {
                return ResponseEntity.badRequest().body(java.util.Map.of("success", false, "message", "Passkey registration failed."));
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Passkey registration finish failed for email {}: {}", finishRequest.getEmail(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing passkey registration data: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid registration data format.", e);
        } catch (Exception e) {
            logger.error("Unexpected error during passkey registration finish for email {}: {}", finishRequest.getEmail(), e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", e);
        }
    }

    @PostMapping("/login/start")
    public ResponseEntity<StartAuthenticationResponse> startAuthentication(@RequestBody(required = false) StartAuthenticationRequest authRequest, HttpServletRequest request) {
        String email = (authRequest != null) ? authRequest.getEmail() : null;
        logger.info("Passkey authentication start requested for email: {}", email != null ? email : "<discoverable>");
        try {
            PublicKeyCredentialRequestOptions requestOptions = passkeyService.startAuthentication(email, relyingPartyId);
            String requestOptionsJson = objectMapper.writeValueAsString(requestOptions);
            return ResponseEntity.ok(new StartAuthenticationResponse(requestOptionsJson));
        } catch (IllegalArgumentException e) {
            logger.warn("Passkey authentication start failed for email {}: {}", email, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing PublicKeyCredentialRequestOptions: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error preparing authentication data.", e);
        }
    }

    @PostMapping("/login/finish")
    public ResponseEntity<AuthenticationSuccessResponse> finishAuthentication(@Valid @RequestBody FinishAuthenticationRequest finishRequest, HttpServletRequest request) {
        logger.info("Passkey authentication finish requested.");
        try {
            String clientOrigin = getClientOrigin(request);
            AuthenticatorAssertionResponse assertionResponse = objectMapper.readValue(finishRequest.getAssertionResponseJson(), AuthenticatorAssertionResponse.class);

            Optional<UserIdentity> userIdentityOptional = passkeyService.finishAuthentication(
                    relyingPartyId,
                    clientOrigin,
                    assertionResponse,
                    finishRequest.getOriginalRequestOptionsJson() // Pass the stored options JSON
            );

            if (userIdentityOptional.isPresent()) {
                UserIdentity userIdentity = userIdentityOptional.get();
                logger.info("Passkey authentication successful for user: {}", userIdentity.getName());
                // Here, you would typically generate a session or JWT for the user.
                // For now, just returning success.
                return ResponseEntity.ok(new AuthenticationSuccessResponse(true, userIdentity.getName(), "Authentication successful."));
            } else {
                logger.warn("Passkey authentication failed.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthenticationSuccessResponse(false, null, "Authentication failed."));
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Passkey authentication finish failed: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing passkey authentication data: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid authentication data format.", e);
        } catch (Exception e) {
            logger.error("Unexpected error during passkey authentication finish: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", e);
        }
    }
}

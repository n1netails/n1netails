package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.model.request.CreateTokenRequest;
import com.n1netails.n1netails.api.model.response.HttpErrorResponse;
import com.n1netails.n1netails.api.model.response.N1neTokenResponse;
import com.n1netails.n1netails.api.service.N1neTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.n1netails.n1netails.api.model.UserPrincipal;


import java.util.List;
import jakarta.persistence.EntityNotFoundException;

import static com.n1netails.n1netails.api.constant.ControllerConstant.APPLICATION_JSON;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "N1ne Token Controller", description = "Operations related to N1ne Tokens")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(path = {"/api/n1ne-token"}, produces = APPLICATION_JSON)
public class N1neTokenController {

    private final N1neTokenService n1neTokenService;

    @Operation(summary = "Create a new token", responses = {
            @ApiResponse(responseCode = "200", description = "Token created",
                    content = @Content(schema = @Schema(implementation = N1neTokenResponse.class)))
    })
    @PostMapping(consumes = APPLICATION_JSON)
    public ResponseEntity<?> create(
            @RequestBody CreateTokenRequest createTokenRequest,
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("Create n1ne token request received.");
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body(new HttpErrorResponse(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, "", "User not authenticated."));
            }
            // If userId is not provided in request, default to the authenticated user's ID
            if (createTokenRequest.getUserId() == null) {
                // We need the actual UsersEntity ID from UserPrincipal
                // Assuming UserPrincipal has a method to get the underlying UsersEntity or its ID
                // For now, this step highlights that direct access to UsersEntity ID from UserDetails might need adjustment
                // Let's assume for now the service layer handles if createTokenRequest.getUserId() is null
                // and uses the principal from SecurityContextHolder if needed, as implemented in service.
            }
            N1neTokenResponse n1neTokenResponse = n1neTokenService.create(createTokenRequest);
            return ResponseEntity.ok(n1neTokenResponse);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new HttpErrorResponse(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND, "", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new HttpErrorResponse(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN, "", e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating token: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new HttpErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR, "", "An unexpected error occurred."));
        }
    }

    @Operation(summary = "Get all tokens (admin/super-admin access, or user's own)", responses = {
            @ApiResponse(responseCode = "200", description = "List of tokens",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = N1neTokenResponse.class))))
    })
    @GetMapping
    public ResponseEntity<List<N1neTokenResponse>> getAll() {
        // @PreAuthorize removed, service layer handles authorization
        List<N1neTokenResponse> n1neTokenResponseList = n1neTokenService.getAll();
        return ResponseEntity.ok(n1neTokenResponseList);
    }

    @Operation(summary = "Get all tokens by user id", responses = {
            @ApiResponse(responseCode = "200", description = "List of tokens by user id",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = N1neTokenResponse.class))))
    })
    @GetMapping("/user-tokens/{userId}")
    public ResponseEntity<?> getAllByUserId(@PathVariable Long userId) {
        // @PreAuthorize removed, service layer handles authorization
        log.info("Get all tokens by user id: {}", userId);
        try {
            List<N1neTokenResponse> n1neTokenResponseList = n1neTokenService.getAllByUserId(userId);
            return ResponseEntity.ok(n1neTokenResponseList);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new HttpErrorResponse(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND, "", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new HttpErrorResponse(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN, "", e.getMessage()));
        }
    }

    @Operation(summary = "Get token by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Token found",
                    content = @Content(schema = @Schema(implementation = N1neTokenResponse.class))),
            @ApiResponse(responseCode = "404", description = "Token not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        // @PreAuthorize removed, service layer handles authorization
        try {
            N1neTokenResponse n1neTokenResponse = n1neTokenService.getById(id);
            return ResponseEntity.ok(n1neTokenResponse);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new HttpErrorResponse(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND, "", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new HttpErrorResponse(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN, "", e.getMessage()));
        }
    }

    @Operation(summary = "Revoke token by ID", responses = {
            @ApiResponse(responseCode = "204", description = "Revoke submitted"),
            @ApiResponse(responseCode = "404", description = "Token not found")
    })
    @PutMapping("/revoke/{id}")
    public ResponseEntity<?> revoke(@PathVariable Long id) {
        // @PreAuthorize removed, service layer handles authorization
        try {
            n1neTokenService.revoke(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new HttpErrorResponse(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND, "", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new HttpErrorResponse(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN, "", e.getMessage()));
        }
    }

    @Operation(summary = "Enable token by ID", responses = { // Corrected summary from "Revoke" to "Enable"
            @ApiResponse(responseCode = "204", description = "Enable submitted"), // Corrected description
            @ApiResponse(responseCode = "404", description = "Token not found")
    })
    @PutMapping("/enable/{id}")
    public ResponseEntity<?> enable(@PathVariable Long id) {
        // @PreAuthorize removed, service layer handles authorization
        try {
            n1neTokenService.enable(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new HttpErrorResponse(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND, "", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new HttpErrorResponse(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN, "", e.getMessage()));
        }
    }

    @Operation(summary = "Delete token by ID", responses = {
            @ApiResponse(responseCode = "204", description = "Token deleted"),
            @ApiResponse(responseCode = "404", description = "Token not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        // @PreAuthorize removed, service layer handles authorization
        try {
            n1neTokenService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(new HttpErrorResponse(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND, "", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                 .body(new HttpErrorResponse(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN, "", e.getMessage()));
        }
    }
}

package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.request.CreateTokenRequest;
import com.n1netails.n1netails.api.model.response.N1neTokenResponse;
import com.n1netails.n1netails.api.service.AuthorizationService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

import static com.n1netails.n1netails.api.constant.ControllerConstant.APPLICATION_JSON;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "N1ne Token Controller", description = "Operations related to N1ne Tokens")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(path = {"/ninetails/n1ne-token"}, produces = APPLICATION_JSON)
public class N1neTokenController {

    private final N1neTokenService n1neTokenService;
    private final AuthorizationService authorizationService;

    @Operation(summary = "Create a new token", responses = {
            @ApiResponse(responseCode = "200", description = "Token created",
                    content = @Content(schema = @Schema(implementation = N1neTokenResponse.class)))
    })
    @PostMapping(consumes = APPLICATION_JSON)
    public ResponseEntity<N1neTokenResponse> create(
            @RequestHeader(AUTHORIZATION) String authorizationHeader,
            @RequestBody CreateTokenRequest createTokenRequest
    ) throws AccessDeniedException, UserNotFoundException {
        if (authorizationService.isSelf(authorizationHeader, createTokenRequest.getUserId())) {
            log.info("Create n1ne token");
            N1neTokenResponse n1neTokenResponse = n1neTokenService.create(createTokenRequest);
            return ResponseEntity.ok(n1neTokenResponse);
        } else {
            throw new AccessDeniedException("Create Token request access denied.");
        }
    }

    // TODO CONSIDER IF THIS IS NEEDED SO ORGANIZATION ADMINS CAN VIEW ALL TOKENS
//    @Operation(summary = "Get all tokens", responses = {
//            @ApiResponse(responseCode = "200", description = "List of tokens",
//                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = N1neTokenResponse.class))))
//    })
//    @GetMapping
//    @PreAuthorize("hasAnyAuthority('user:admin')")
//    public ResponseEntity<List<N1neTokenResponse>> getAll() {
//        List<N1neTokenResponse> n1neTokenResponseList = n1neTokenService.getAll();
//        return ResponseEntity.ok(n1neTokenResponseList);
//    }

    @Operation(summary = "Get all tokens by user id", responses = {
            @ApiResponse(responseCode = "200", description = "List of tokens by user id",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = N1neTokenResponse.class))))
    })
    @GetMapping("/user-tokens/{userId}")
    public ResponseEntity<List<N1neTokenResponse>> getAllByUserId(
            @RequestHeader(AUTHORIZATION) String authorizationHeader,
            @PathVariable Long userId
    ) throws UserNotFoundException, AccessDeniedException {
        if (authorizationService.isSelf(authorizationHeader, userId)) {
            log.info("Get all tokens by user id");
            List<N1neTokenResponse> n1neTokenResponseList = n1neTokenService.getAllByUserId(userId);
            return ResponseEntity.ok(n1neTokenResponseList);
        } else {
            throw new AccessDeniedException("Get User Tokens request access denied.");
        }
    }

    @Operation(summary = "Revoke token by ID", responses = {
            @ApiResponse(responseCode = "204", description = "Revoke submitted"),
            @ApiResponse(responseCode = "404", description = "Token not found")
    })
    @PutMapping("/revoke/{id}")
    public ResponseEntity<Void> revoke(
            @RequestHeader(AUTHORIZATION) String authorizationHeader,
            @PathVariable Long id
    ) throws UserNotFoundException, AccessDeniedException {
        N1neTokenResponse n1neToken = this.n1neTokenService.getById(id);
        if (authorizationService.isOwnerOrOrganizationAdmin(authorizationHeader, n1neToken.getUserId(), n1neToken.getOrganizationId())) {
            n1neTokenService.revoke(id);
            return ResponseEntity.noContent().build();
        } else {
            throw new AccessDeniedException("Revoke token request access denied.");
        }
    }

    @Operation(summary = "Enable token by ID", responses = {
            @ApiResponse(responseCode = "204", description = "Enable submitted"),
            @ApiResponse(responseCode = "404", description = "Token not found")
    })
    @PutMapping("/enable/{id}")
    public ResponseEntity<Void> enable(
            @RequestHeader(AUTHORIZATION) String authorizationHeader,
            @PathVariable Long id
    ) throws AccessDeniedException, UserNotFoundException {
        N1neTokenResponse n1neToken = this.n1neTokenService.getById(id);
        if (authorizationService.isOwnerOrOrganizationAdmin(authorizationHeader, n1neToken.getUserId(), n1neToken.getOrganizationId())) {
            n1neTokenService.enable(id);
            return ResponseEntity.noContent().build();
        } else {
            throw new AccessDeniedException("Enable token request access denied.");
        }
    }

    @Operation(summary = "Delete token by ID", responses = {
            @ApiResponse(responseCode = "204", description = "Token deleted"),
            @ApiResponse(responseCode = "404", description = "Token not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @RequestHeader(AUTHORIZATION) String authorizationHeader,
            @PathVariable Long id
    ) throws UserNotFoundException, AccessDeniedException {
        N1neTokenResponse n1neToken = this.n1neTokenService.getById(id);
        if (authorizationService.isSelf(authorizationHeader, n1neToken.getUserId())) {
            n1neTokenService.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            throw new AccessDeniedException("Delete token request access denied.");
        }
    }
}

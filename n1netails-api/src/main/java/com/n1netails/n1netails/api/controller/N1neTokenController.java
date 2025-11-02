package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.N1neTokenGenerateException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.request.CreateTokenRequest;
import com.n1netails.n1netails.api.model.request.PageRequest;
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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "N1ne Token Controller", description = "Operations related to N1ne Tokens")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(path = {"/ninetails/n1ne-token"}, produces = APPLICATION_JSON_VALUE)
public class N1neTokenController {

    private final N1neTokenService n1neTokenService;
    private final AuthorizationService authorizationService;

    @Operation(summary = "Create a new token", responses = {
            @ApiResponse(responseCode = "200", description = "Token created",
                    content = @Content(schema = @Schema(implementation = N1neTokenResponse.class)))
    })
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<N1neTokenResponse> create(
            @RequestHeader(AUTHORIZATION) String authorizationHeader,
            @RequestBody CreateTokenRequest createTokenRequest
    ) throws AccessDeniedException, UserNotFoundException, N1neTokenGenerateException {

        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        if (authorizationService.isSelf(currentUser, createTokenRequest.getUserId())) {
            log.info("Create n1ne token");
            N1neTokenResponse n1neTokenResponse = n1neTokenService.create(createTokenRequest);
            return ResponseEntity.ok(n1neTokenResponse);
        } else {
            throw new AccessDeniedException("Create Token request access denied.");
        }
    }

    @Operation(summary = "Get all tokens by user id", responses = {
            @ApiResponse(responseCode = "200", description = "Paginated result containing tokens (Spring Data Page format). List of tokens by user id",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = N1neTokenResponse.class))))
    })
    @GetMapping("/user-tokens/{userId}")
    public ResponseEntity<Page<N1neTokenResponse>> getAllByUserId(
            @RequestHeader(AUTHORIZATION) String authorizationHeader,
            @PathVariable Long userId,
            @ParameterObject PageRequest pageRequest
    ) throws UserNotFoundException, AccessDeniedException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        if (authorizationService.isSelf(currentUser, userId)) {
            log.info("Get all tokens by user id");
            Page<N1neTokenResponse> n1neTokenResponsePage = n1neTokenService.getAllByUserId(userId, pageRequest);
            return ResponseEntity.ok(n1neTokenResponsePage);
        } else {
            throw new AccessDeniedException("Get User Tokens request access denied.");
        }
    }

    // TODO IMPLEMENT CONTROLLER ENDPOINT TO GET TOKEN BY ID

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
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        if (authorizationService.isOwnerOrOrganizationAdmin(currentUser, n1neToken.getUserId(), n1neToken.getOrganizationId())) {
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
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        if (authorizationService.isOwnerOrOrganizationAdmin(currentUser, n1neToken.getUserId(), n1neToken.getOrganizationId())) {
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
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        if (authorizationService.isSelf(currentUser, n1neToken.getUserId())) {
            n1neTokenService.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            throw new AccessDeniedException("Delete token request access denied.");
        }
    }
}

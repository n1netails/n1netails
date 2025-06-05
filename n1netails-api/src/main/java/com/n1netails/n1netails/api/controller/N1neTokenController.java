package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.request.CreateTokenRequest;
import com.n1netails.n1netails.api.model.response.N1neTokenResponse;
import com.n1netails.n1netails.api.service.AuthorizationService;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.service.N1neTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import org.springframework.security.access.AccessDeniedException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import java.nio.file.AccessDeniedException; // Replaced
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Operation(summary = "Get all tokens (Super Admin or Org Admin)", responses = {
            @ApiResponse(responseCode = "200", description = "List of tokens",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = N1neTokenResponse.class))))
    })
    @GetMapping
    public ResponseEntity<List<N1neTokenResponse>> getAll(@RequestHeader(AUTHORIZATION) String authorizationHeader) throws UserNotFoundException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        List<N1neTokenResponse> responseList;

        if (authorizationService.isSuperAdmin(currentUser)) {
            log.info("Super Admin {} fetching all N1ne tokens.", currentUser.getUsername());
            responseList = n1neTokenService.getAllTokens();
        } else {
            Set<Long> adminOrgIds = currentUser.getOrganizations().stream()
                    .filter(org -> {
                        // We need to call isOrganizationAdmin with the principal and org ID.
                        // The principal passed to isOrganizationAdmin should be the currentUser.
                        return authorizationService.isOrganizationAdmin(currentUser, org.getId());
                    })
                    .map(OrganizationEntity::getId)
                    .collect(Collectors.toSet());

            if (!adminOrgIds.isEmpty()) {
                log.info("Org Admin {} fetching N1ne tokens for organizations: {}", currentUser.getUsername(), adminOrgIds);
                responseList = n1neTokenService.getAllTokensForOrganizations(adminOrgIds);
            } else {
                log.info("User {} is not a Super Admin and not an admin of any organization. Returning empty list of tokens.", currentUser.getUsername());
                responseList = Collections.emptyList();
            }
        }
        return ResponseEntity.ok(responseList);
    }

    @Operation(summary = "Get all tokens by user id", responses = {
            @ApiResponse(responseCode = "200", description = "List of tokens by user id",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = N1neTokenResponse.class))))
    })
    @GetMapping("/user-tokens/{userId}")
    public ResponseEntity<List<N1neTokenResponse>> getAllByUserId(
            @RequestHeader(AUTHORIZATION) String authorizationHeader,
            @PathVariable Long userId
    ) throws UserNotFoundException, AccessDeniedException {
        // For fetching tokens by user ID, ensuring the caller is the user themselves OR a super admin.
        // Org admin check could be added if org admins should see tokens of users in their org.
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        if (currentUser.getId().equals(userId) || authorizationService.isSuperAdmin(currentUser)) {
            log.info("User {} fetching tokens for user ID: {}. Caller isSelf: {}, isSuperAdmin: {}",
                    currentUser.getUsername(), userId, currentUser.getId().equals(userId), authorizationService.isSuperAdmin(currentUser));
            List<N1neTokenResponse> n1neTokenResponseList = n1neTokenService.getAllByUserId(userId);
            return ResponseEntity.ok(n1neTokenResponseList);
        } else {
            log.warn("User {} attempted to fetch tokens for user ID {} without sufficient privileges.", currentUser.getUsername(), userId);
            throw new AccessDeniedException("You do not have permission to view these tokens.");
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
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        N1neTokenResponse n1neToken = this.n1neTokenService.getById(id); // Fetches the token details

        // Check if the current user is the owner of the token, or an admin of the token's organization, or a super admin
        boolean isOwner = currentUser.getId().equals(n1neToken.getUserId());
        boolean isOrgAdmin = authorizationService.isOrganizationAdmin(currentUser, n1neToken.getOrganizationId());
        boolean isSuperAdmin = authorizationService.isSuperAdmin(currentUser);

        if (isOwner || isOrgAdmin || isSuperAdmin) {
            log.info("User {} revoking token ID: {}. IsOwner: {}, IsOrgAdmin: {}, IsSuperAdmin: {}",
                    currentUser.getUsername(), id, isOwner, isOrgAdmin, isSuperAdmin);
            n1neTokenService.revoke(id);
            return ResponseEntity.noContent().build();
        } else {
            log.warn("User {} attempted to revoke token ID {} without sufficient privileges.", currentUser.getUsername(), id);
            throw new AccessDeniedException("You do not have permission to revoke this token.");
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
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        N1neTokenResponse n1neToken = this.n1neTokenService.getById(id);

        boolean isOwner = currentUser.getId().equals(n1neToken.getUserId());
        boolean isOrgAdmin = authorizationService.isOrganizationAdmin(currentUser, n1neToken.getOrganizationId());
        boolean isSuperAdmin = authorizationService.isSuperAdmin(currentUser);

        if (isOwner || isOrgAdmin || isSuperAdmin) {
            log.info("User {} enabling token ID: {}. IsOwner: {}, IsOrgAdmin: {}, IsSuperAdmin: {}",
                    currentUser.getUsername(), id, isOwner, isOrgAdmin, isSuperAdmin);
            n1neTokenService.enable(id);
            return ResponseEntity.noContent().build();
        } else {
            log.warn("User {} attempted to enable token ID {} without sufficient privileges.", currentUser.getUsername(), id);
            throw new AccessDeniedException("You do not have permission to enable this token.");
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
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        N1neTokenResponse n1neToken = this.n1neTokenService.getById(id);

        // Typically, only owners or super admins can delete. Org admins might only revoke/disable.
        boolean isOwner = currentUser.getId().equals(n1neToken.getUserId());
        boolean isSuperAdmin = authorizationService.isSuperAdmin(currentUser);

        if (isOwner || isSuperAdmin) {
            log.info("User {} deleting token ID: {}. IsOwner: {}, IsSuperAdmin: {}",
                    currentUser.getUsername(), id, isOwner, isSuperAdmin);
            n1neTokenService.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            log.warn("User {} attempted to delete token ID {} without sufficient privileges.", currentUser.getUsername(), id);
            throw new AccessDeniedException("You do not have permission to delete this token.");
        }
    }
}

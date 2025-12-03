package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.model.request.OrganizationRequest;
import com.n1netails.n1netails.api.service.AuthorizationService;
import com.n1netails.n1netails.api.service.OrganizationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Organization Controller", description = "Operations related to Organizations")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(path = {"/ninetails/organizations"}, produces = APPLICATION_JSON_VALUE)
public class OrganizationController {

    private final OrganizationService organizationService;
    private final AuthorizationService authorizationService;

    @Operation(
        summary = "Create a new organization",
        responses = {
            @ApiResponse(responseCode = "201", description = "Organization created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
        }
    )
    @PostMapping
    @PreAuthorize("hasAuthority('user:super')") // Corresponds to SUPER_ADMIN_AUTHORITIES which includes 'user:super'
    public ResponseEntity<OrganizationEntity> createOrganization(@Valid @RequestBody OrganizationRequest organizationRequest) {
        OrganizationEntity newOrganization = organizationService.createOrganization(organizationRequest);
        return new ResponseEntity<>(newOrganization, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Get all organizations",
        responses = {
            @ApiResponse(responseCode = "200", description = "List of organizations")
        }
    )
    @GetMapping
    @PreAuthorize("hasAuthority('user:super')")
    public ResponseEntity<List<OrganizationEntity>> getAllOrganizations() {
        List<OrganizationEntity> organizations = organizationService.getAllOrganizations();
        return ResponseEntity.ok(organizations);
    }

    // Endpoints for Super Admin to manage any organization's users
     @Operation(
        summary = "Add user to organization (Super Admin)",
        responses = {
            @ApiResponse(responseCode = "200", description = "User added"),
            @ApiResponse(responseCode = "404", description = "Organization or user not found")
        }
    )
    @PostMapping("/{organizationId}/users/{userId}")
    @PreAuthorize("hasAuthority('user:super')")
    public ResponseEntity<Void> addUserToOrganization(@PathVariable Long organizationId, @PathVariable Long userId) {
        organizationService.addUserToOrganization(organizationId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Remove user from organization (Super Admin)",
        responses = {
            @ApiResponse(responseCode = "200", description = "User removed"),
            @ApiResponse(responseCode = "404", description = "Organization or user not found")
        }
    )
    @DeleteMapping("/{organizationId}/users/{userId}")
    @PreAuthorize("hasAuthority('user:super')")
    public ResponseEntity<Void> removeUserFromOrganization(@PathVariable Long organizationId, @PathVariable Long userId) {
        organizationService.removeUserFromOrganization(organizationId, userId);
        return ResponseEntity.ok().build();
    }

    // Endpoints for Admin (of an org) to manage their own organization's members
    @Operation(
        summary = "Add member to organization (Admin)",
        responses = {
            @ApiResponse(responseCode = "200", description = "Member added"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User or organization not found")
        }
    )
    @PostMapping("/{organizationId}/members/{targetUserId}")
    @PreAuthorize("hasAuthority('user:admin')")
    public ResponseEntity<Void> addMemberToOrganization(
            @RequestHeader(AUTHORIZATION) String authorizationHeader,
            @PathVariable Long organizationId,
            @PathVariable Long targetUserId) throws UserNotFoundException {

        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        if (!authorizationService.isOrganizationAdmin(currentUser, organizationId)) {
            log.warn("User {} attempted to add member to organization {} without being an admin.", currentUser.getUsername(), organizationId);
            throw new AccessDeniedException("User is not an admin of this organization.");
        }
        log.info("Admin {} adding member {} to organization {}", currentUser.getUsername(), targetUserId, organizationId);
        organizationService.addMemberToMyOrganization(organizationId, targetUserId, currentUser.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Remove member from organization (Admin)",
        responses = {
            @ApiResponse(responseCode = "200", description = "Member removed"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User or organization not found")
        }
    )
    @DeleteMapping("/{organizationId}/members/{targetUserId}")
    @PreAuthorize("hasAuthority('user:admin')")
    public ResponseEntity<Void> removeMemberFromOrganization(
            @RequestHeader(AUTHORIZATION) String authorizationHeader,
            @PathVariable Long organizationId,
            @PathVariable Long targetUserId) throws UserNotFoundException {

        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        if (!authorizationService.isOrganizationAdmin(currentUser, organizationId)) {
            log.warn("User {} attempted to remove member from organization {} without being an admin.", currentUser.getUsername(), organizationId);
            throw new AccessDeniedException("User is not an admin of this organization.");
        }
        log.info("Admin {} removing member {} from organization {}", currentUser.getUsername(), targetUserId, organizationId);
        organizationService.removeMemberFromMyOrganization(organizationId, targetUserId, currentUser.getUsername());
        return ResponseEntity.ok().build();
    }
}

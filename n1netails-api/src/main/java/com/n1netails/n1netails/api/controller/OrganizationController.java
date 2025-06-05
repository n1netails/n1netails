package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.model.request.OrganizationRequest;
import com.n1netails.n1netails.api.service.OrganizationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.n1netails.n1netails.api.constant.ControllerConstant.APPLICATION_JSON;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Organization Controller", description = "Operations related to Organizations")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(path = {"/ninetails/organizations"}, produces = APPLICATION_JSON)
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    @PreAuthorize("hasAuthority('user:super')") // Corresponds to SUPER_ADMIN_AUTHORITIES which includes 'user:super'
    public ResponseEntity<OrganizationEntity> createOrganization(@Valid @RequestBody OrganizationRequest organizationRequest) {
        OrganizationEntity newOrganization = organizationService.createOrganization(organizationRequest);
        return new ResponseEntity<>(newOrganization, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('user:super')")
    public ResponseEntity<List<OrganizationEntity>> getAllOrganizations() {
        List<OrganizationEntity> organizations = organizationService.getAllOrganizations();
        return ResponseEntity.ok(organizations);
    }

    // Endpoints for Super Admin to manage any organization's users
    @PostMapping("/{organizationId}/users/{userId}")
    @PreAuthorize("hasAuthority('user:super')")
    public ResponseEntity<Void> addUserToOrganization(@PathVariable Long organizationId, @PathVariable Long userId) {
        organizationService.addUserToOrganization(organizationId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{organizationId}/users/{userId}")
    @PreAuthorize("hasAuthority('user:super')")
    public ResponseEntity<Void> removeUserFromOrganization(@PathVariable Long organizationId, @PathVariable Long userId) {
        organizationService.removeUserFromOrganization(organizationId, userId);
        return ResponseEntity.ok().build();
    }

    // TODO POSSIBLY REFACTOR THE @AuthenticationPrincipal TO INSTEAD AUTHENTICATE THE JWT TOKEN
    // Endpoints for Admin (of an org) to manage their own organization's members
    @PostMapping("/{organizationId}/members/{targetUserId}")
    @PreAuthorize("hasAuthority('user:admin')") // 'user:create' is in ADMIN_AUTHORITIES and SUPER_ADMIN_AUTHORITIES
    public ResponseEntity<Void> addMemberToOrganization(@PathVariable Long organizationId, @PathVariable Long targetUserId, @AuthenticationPrincipal UserDetails adminPrincipal) {
        if (adminPrincipal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        organizationService.addMemberToMyOrganization(organizationId, targetUserId, adminPrincipal.getUsername());
        return ResponseEntity.ok().build();
    }

    // TODO POSSIBLY REFACTOR THE @AuthenticationPrincipal TO INSTEAD AUTHENTICATE THE JWT TOKEN
    @DeleteMapping("/{organizationId}/members/{targetUserId}")
    @PreAuthorize("hasAuthority('user:admin')")
    public ResponseEntity<Void> removeMemberFromOrganization(@PathVariable Long organizationId, @PathVariable Long targetUserId, @AuthenticationPrincipal UserDetails adminPrincipal) {
        if (adminPrincipal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        organizationService.removeMemberFromMyOrganization(organizationId, targetUserId, adminPrincipal.getUsername());
        return ResponseEntity.ok().build();
    }
}

package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.model.request.OrganizationRequestDto;
import com.n1netails.n1netails.api.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/organizations") // Using a versioned API path
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    @PreAuthorize("hasAuthority('user:super')") // Corresponds to SUPER_ADMIN_AUTHORITIES which includes 'user:admin'
    public ResponseEntity<OrganizationEntity> createOrganization(@Valid @RequestBody OrganizationRequestDto organizationDto) {
        OrganizationEntity newOrganization = organizationService.createOrganization(organizationDto);
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

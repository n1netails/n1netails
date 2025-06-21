package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.model.entity.TailEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.repository.TailRepository;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.n1netails.n1netails.api.constant.ProjectSecurityConstant.TOKEN_PREFIX;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("authorizationService")
public class AuthorizationServiceImpl implements AuthorizationService {

    private final UserRepository userRepository;
    private final JwtDecoder jwtDecoder;
    private final TailRepository tailRepository;

    @Override
    public UserPrincipal getCurrentUserPrincipal(String authorizationHeader) throws UserNotFoundException {
        log.debug("Attempting to get current user principal from authorization header.");
        String token = authorizationHeader.replace(TOKEN_PREFIX, "");
        String authEmail = jwtDecoder.decode(token).getSubject();
        log.info("Decoded email from token: {}", authEmail);

        UsersEntity currentUser = userRepository.findUserByEmail(authEmail)
                .orElseThrow(() -> {
                    log.warn("User not found for email: {}", authEmail);
                    return new UserNotFoundException("User not found by provided email from token.");
                });
        log.debug("User entity found for email: {}", authEmail);
        return new UserPrincipal(currentUser);
    }

    @Override
    public boolean isOrganizationAdmin(UserPrincipal principal, Long organizationId) {
        log.debug("Checking if user {} is admin for organization {}", principal.getUsername(), organizationId);
        boolean isAdmin = principal.getAuthorities().contains(new SimpleGrantedAuthority("user:admin")) &&
                principal.getOrganizations().stream()
                        .map(OrganizationEntity::getId)
                        .anyMatch(id -> id.equals(organizationId));
        log.info("User {} is{} admin for organization {}", principal.getUsername(), isAdmin ? "" : " not", organizationId);
        return isAdmin;
    }

    @Override
    public boolean isSuperAdmin(UserPrincipal principal) {
        log.debug("Checking if user {} is super admin", principal.getUsername());
        boolean isSuper = principal.getAuthorities().contains(new SimpleGrantedAuthority("user:super"));
        log.info("User {} is{} super admin", principal.getUsername(), isSuper ? "" : " not");
        return isSuper;
    }

    @Override
    public boolean belongsToOrganization(UserPrincipal principal, Long organizationId) {
        log.debug("Checking if user {} belongs to organization {}", principal.getUsername(), organizationId);
        boolean belongs = principal.getOrganizations().stream()
                .map(OrganizationEntity::getId)
                .anyMatch(id -> id.equals(organizationId));
        log.info("User {} {} to organization {}", principal.getUsername(), belongs ? "belongs" : "does not belong", organizationId);
        return belongs;
    }

    @Override
    public boolean isTailOwner(UserPrincipal principal, Long tailUserId) {
        log.debug("Checking if user {} is owner of tail with user ID {}", principal.getUsername(), tailUserId);
        boolean isOwner = Objects.equals(principal.getId(), tailUserId);
        log.info("User {} is{} owner of tail with user ID {}", principal.getUsername(), isOwner ? "" : " not", tailUserId);
        return isOwner;
    }

    @Override
    public boolean isSelf(UserPrincipal principal, Long userIdToCheck) {
        log.info("is self check for user ID {} against principal ID {}", userIdToCheck, principal.getId());
        return Objects.equals(principal.getId(), userIdToCheck);
    }

    @Override
    public boolean isOwnerOrOrganizationAdmin(UserPrincipal principal, Long ownerUserId, Long organizationId) {
        log.debug("Checking if user {} is owner (ID: {}) or admin for organization {}", principal.getUsername(), ownerUserId, organizationId);
        if (isSelf(principal, ownerUserId)) {
            log.info("User {} is owner (ID: {})", principal.getUsername(), ownerUserId);
            return true;
        }
        if (isOrganizationAdmin(principal, organizationId)) {
            log.info("User {} is admin for organization {}", principal.getUsername(), organizationId);
            return true;
        }
        log.info("User {} is neither owner (ID: {}) nor admin for organization {}", principal.getUsername(), ownerUserId, organizationId);
        return false;
    }

    @Override
    public boolean isUserAssignedToTail(UserPrincipal principal, Long tailId) {
        log.debug("Checking if user {} is assigned to tail {}", principal.getUsername(), tailId);
        if (principal == null || tailId == null) {
            return false;
        }
        // Super admins and organization admins of the tail's organization can also access notes
        // So, first fetch the tail to check its organization and assigned user.
        TailEntity tail = tailRepository.findById(tailId).orElse(null);
        if (tail == null) {
            log.warn("Tail not found for ID: {} during authorization check", tailId);
            return false; // Or throw TailNotFoundException if preferred, but for auth check, false is safer.
        }

        // Check if user is directly assigned
        if (principal.getId().equals(tail.getAssignedUserId())) {
            log.info("User {} is directly assigned to tail {}", principal.getUsername(), tailId);
            return true;
        }

        // Check if user is an admin of the organization the tail belongs to
        if (tail.getOrganization() != null && isOrganizationAdmin(principal, tail.getOrganization().getId())) {
            log.info("User {} is an admin of the organization {} for tail {}", principal.getUsername(), tail.getOrganization().getId(), tailId);
            return true;
        }

        // Check if user is super admin
        if(isSuperAdmin(principal)){
            log.info("User {} is Super Admin and can access tail {}", principal.getUsername(), tailId);
            return true;
        }

        log.info("User {} is not authorized for tail {}", principal.getUsername(), tailId);
        return false;
    }
}

package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.N1neTokenEntity;
import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.CreateTokenRequest;
import com.n1netails.n1netails.api.model.response.N1neTokenResponse;
import com.n1netails.n1netails.api.repository.N1neTokenRepository;
import com.n1netails.n1netails.api.repository.OrganizationRepository;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.service.N1neTokenService;
import com.n1netails.n1netails.api.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("n1neTokenService")
public class N1neTokenServiceImpl implements N1neTokenService {

    public static final String TOKEN_DOES_NOT_EXIST = "Token does not exist: ";
    public static final String USER_DOES_NOT_EXIST = "User does not exist: ";
    public static final String ORG_DOES_NOT_EXIST = "Organization does not exist: ";
    public static final String ACCESS_DENIED_MSG = "Access Denied";

    private final N1neTokenRepository n1neTokenRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final OrganizationRepository organizationRepository;


    @Override
    public N1neTokenResponse create(CreateTokenRequest createTokenRequest) {
        UserPrincipal principal = getCurrentUserPrincipal();
        UsersEntity requester = userService.findUserByEmail(principal.getUsername());

        Long targetUserId = createTokenRequest.getUserId() != null ? createTokenRequest.getUserId() : requester.getId();
        UsersEntity tokenOwner = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException(USER_DOES_NOT_EXIST + targetUserId));

        OrganizationEntity tokenOrganization = null;
        if (createTokenRequest.getOrganizationId() != null) {
            tokenOrganization = organizationRepository.findById(createTokenRequest.getOrganizationId())
                    .orElseThrow(() -> new EntityNotFoundException(ORG_DOES_NOT_EXIST + createTokenRequest.getOrganizationId()));
            // Authorization: SuperAdmin can assign to any org. Admin can only assign to their own orgs.
            if (!isSuperAdmin(requester) && !requester.getOrganizations().contains(tokenOrganization)) {
                throw new AccessDeniedException("Admin " + requester.getEmail() + " cannot create token for organization " + tokenOrganization.getName());
            }
        } else {
            // Default organization logic
            Set<OrganizationEntity> ownerOrgs = tokenOwner.getOrganizations();
            if (ownerOrgs != null && !ownerOrgs.isEmpty()) {
                Optional<OrganizationEntity> nonN1netailsOrg = ownerOrgs.stream()
                        .filter(org -> !"n1netails".equalsIgnoreCase(org.getName()))
                        .findFirst();
                if (nonN1netailsOrg.isPresent()) {
                    tokenOrganization = nonN1netailsOrg.get();
                } else {
                    tokenOrganization = ownerOrgs.stream()
                        .filter(org -> "n1netails".equalsIgnoreCase(org.getName()))
                        .findFirst().orElse(null); // Should be present if user is in any org
                }
            }
        }

        N1neTokenEntity n1neTokenEntity = new N1neTokenEntity();
        n1neTokenEntity.setUser(tokenOwner);
        n1neTokenEntity.setOrganization(tokenOrganization);
        n1neTokenEntity.setCreatedAt(Instant.now());
        n1neTokenEntity.setExpiresAt(createTokenRequest.getExpiresAt());
        n1neTokenEntity.setName(createTokenRequest.getName());
        n1neTokenEntity.setToken(UUID.randomUUID());
        n1neTokenEntity = this.n1neTokenRepository.save(n1neTokenEntity);
        return generateN1neTokenResponse(n1neTokenEntity);
    }

    @Override
    public N1neTokenResponse getById(Long id) {
        N1neTokenEntity token = n1neTokenRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TOKEN_DOES_NOT_EXIST + id));
        UserPrincipal principal = getCurrentUserPrincipal();
        UsersEntity currentUser = userService.findUserByEmail(principal.getUsername());

        if (isSuperAdmin(currentUser) || token.getUser().getId().equals(currentUser.getId()) ||
            (isAdmin(currentUser) && token.getOrganization() != null && currentUser.getOrganizations().contains(token.getOrganization()))) {
            return generateN1neTokenResponse(token);
        }
        throw new AccessDeniedException(ACCESS_DENIED_MSG);
    }

    @Override
    public List<N1neTokenResponse> getAll() {
        UserPrincipal principal = getCurrentUserPrincipal();
        UsersEntity currentUser = userService.findUserByEmail(principal.getUsername());
        List<N1neTokenEntity> n1neTokenEntities;

        if (isSuperAdmin(currentUser)) {
            n1neTokenEntities = n1neTokenRepository.findAll();
        } else if (isAdmin(currentUser)) {
            List<Long> orgIds = currentUser.getOrganizations().stream().map(OrganizationEntity::getId).collect(Collectors.toList());
            n1neTokenEntities = new ArrayList<>(n1neTokenRepository.findByOrganizationIdIn(orgIds));
            // Add tokens that belong to the admin user but have no organization
            n1neTokenEntities.addAll(n1neTokenRepository.findByUserIdAndOrganizationIsNull(currentUser.getId()));
            // Remove duplicates if any (though logic should prevent it if tokens have org or are user's without org)
             n1neTokenEntities = n1neTokenEntities.stream().distinct().collect(Collectors.toList());
        } else { // Regular User
            n1neTokenEntities = n1neTokenRepository.findByUserId(currentUser.getId());
        }
        return n1neTokenEntities.stream().map(N1neTokenServiceImpl::generateN1neTokenResponse).collect(Collectors.toList());
    }

    @Override
    public List<N1neTokenResponse> getAllByUserId(Long userIdToFetch) {
        UserPrincipal principal = getCurrentUserPrincipal();
        UsersEntity currentUser = userService.findUserByEmail(principal.getUsername());

        if (currentUser.getId().equals(userIdToFetch) || isSuperAdmin(currentUser)) {
            return n1neTokenRepository.findByUserId(userIdToFetch).stream()
                    .map(N1neTokenServiceImpl::generateN1neTokenResponse).collect(Collectors.toList());
        }
        if (isAdmin(currentUser)) {
            UsersEntity userToGetTokensFor = userRepository.findById(userIdToFetch)
                    .orElseThrow(() -> new EntityNotFoundException(USER_DOES_NOT_EXIST + userIdToFetch));
            boolean isUserInAdminsOrg = userToGetTokensFor.getOrganizations().stream()
                    .anyMatch(org -> currentUser.getOrganizations().contains(org));
            if (isUserInAdminsOrg) {
                return n1neTokenRepository.findByUserId(userIdToFetch).stream()
                        .map(N1neTokenServiceImpl::generateN1neTokenResponse).collect(Collectors.toList());
            }
        }
        throw new AccessDeniedException(ACCESS_DENIED_MSG);
    }

    private void checkAccessForModification(Long tokenId, UsersEntity currentUser) {
        N1neTokenEntity token = n1neTokenRepository.findById(tokenId)
                .orElseThrow(() -> new EntityNotFoundException(TOKEN_DOES_NOT_EXIST + tokenId));
        if (isSuperAdmin(currentUser)) return;
        if (isAdmin(currentUser) && token.getOrganization() != null && currentUser.getOrganizations().contains(token.getOrganization())) return;
        // Regular users cannot modify arbitrary tokens by ID. They might revoke their own via a different mechanism if needed.
        throw new AccessDeniedException(ACCESS_DENIED_MSG);
    }


    @Override
    public void revoke(Long id) {
        UserPrincipal principal = getCurrentUserPrincipal();
        UsersEntity currentUser = userService.findUserByEmail(principal.getUsername());
        checkAccessForModification(id, currentUser);
        N1neTokenEntity n1neTokenEntity = this.n1neTokenRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TOKEN_DOES_NOT_EXIST + id));
        n1neTokenEntity.setRevoked(true);
        this.n1neTokenRepository.save(n1neTokenEntity);
    }

    @Override
    public void enable(Long id) {
        UserPrincipal principal = getCurrentUserPrincipal();
        UsersEntity currentUser = userService.findUserByEmail(principal.getUsername());
        checkAccessForModification(id, currentUser);
        N1neTokenEntity n1neTokenEntity = this.n1neTokenRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TOKEN_DOES_NOT_EXIST + id));
        n1neTokenEntity.setRevoked(false);
        this.n1neTokenRepository.save(n1neTokenEntity);
    }

    @Override
    public void delete(Long id) {
        UserPrincipal principal = getCurrentUserPrincipal();
        UsersEntity currentUser = userService.findUserByEmail(principal.getUsername());
        checkAccessForModification(id, currentUser);
        this.n1neTokenRepository.deleteById(id);
    }

    @Override
    public boolean validateToken(String n1neToken) {
        log.info("validating token");
        UUID token = UUID.fromString(n1neToken);
        log.info("attempting to locate token");
        Optional<N1neTokenEntity> optionalN1neTokenEntity = this.n1neTokenRepository.findByToken(token);

        if (optionalN1neTokenEntity.isPresent()) {
            log.info("token is present");
            N1neTokenEntity n1neTokenEntity = optionalN1neTokenEntity.get();
            boolean tokenRevoked = n1neTokenEntity.isRevoked();
            // if expiration date is before the current date of validation, return true
            boolean tokenExpired;
            if (n1neTokenEntity.getExpiresAt() != null) tokenExpired = n1neTokenEntity.getExpiresAt().isBefore(Instant.now());
            else tokenExpired = false;
            return !tokenRevoked && !tokenExpired;
        }
        return false;
    }

    private static N1neTokenResponse generateN1neTokenResponse(N1neTokenEntity n1neTokenEntity) {
        N1neTokenResponse n1neTokenResponse = new N1neTokenResponse();
        n1neTokenResponse.setId(n1neTokenEntity.getId());
        n1neTokenResponse.setToken(n1neTokenEntity.getToken());
        n1neTokenResponse.setCreatedAt(n1neTokenEntity.getCreatedAt());
        n1neTokenResponse.setRevoked(n1neTokenEntity.isRevoked());
        n1neTokenResponse.setExpiresAt(n1neTokenEntity.getExpiresAt());
        n1neTokenResponse.setName(n1neTokenEntity.getName());
        n1neTokenResponse.setUserId(n1neTokenEntity.getUser().getId());
        if (n1neTokenEntity.getOrganization() != null)
            n1neTokenResponse.setOrganizationId(n1neTokenEntity.getOrganization().getId());
        return n1neTokenResponse;
    }
}

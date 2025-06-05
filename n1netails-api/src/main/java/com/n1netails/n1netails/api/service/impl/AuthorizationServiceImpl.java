package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.constant.Authority;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.n1netails.n1netails.api.constant.ProjectSecurityConstant.TOKEN_PREFIX;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("authorizationService")
public class AuthorizationServiceImpl implements AuthorizationService {

    private final UserRepository userRepository;
    private final JwtDecoder jwtDecoder;

    @Override
    public boolean isSelf(String authorizationHeader, Long userId) throws UserNotFoundException {
        UserPrincipal principal = getUserPrincipal(authorizationHeader, userId);

        log.info("is self {}", Objects.equals(principal.getId(), userId));
        return Objects.equals(principal.getId(), userId);
    }

    @Override
    public boolean isOwnerOrOrganizationAdmin(String authorizationHeader, Long userId, Long organizationId) throws UserNotFoundException {
        UserPrincipal principal = getUserPrincipal(authorizationHeader, userId);

        log.info("Authorities: {}", principal.getAuthorities().stream().toList());
        Set<OrganizationEntity> organizationEntitySet = principal.getOrganizations();
        List<Long> organizationIds = organizationEntitySet.stream().map(OrganizationEntity::getId).toList();
        if (Objects.equals(principal.getId(), userId)) {
            return true;
        } else if (principal.getAuthorities().contains(new SimpleGrantedAuthority("user:admin")) && organizationIds.contains(organizationId)) {
            return true;
        } else {
            return false;
        }
    }

    private UserPrincipal getUserPrincipal(String authorizationHeader, Long userId) throws UserNotFoundException {
        log.info("USER ID: {}", userId);
        String token = authorizationHeader.substring(TOKEN_PREFIX.length());
        String authEmail = jwtDecoder.decode(token).getSubject();
        log.info("JWT AUTH EMAL: {}", authEmail);
        String claims = jwtDecoder.decode(token).getClaims().toString();
        log.info("JWT CLAIMS: {}", claims);

        UsersEntity currentUser = this.userRepository.findUserByEmail(authEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found by provided email."));

        return new UserPrincipal(currentUser);
    }
}

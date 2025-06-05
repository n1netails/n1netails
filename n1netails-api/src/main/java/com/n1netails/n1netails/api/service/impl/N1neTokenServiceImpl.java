package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.N1neTokenNotFoundException;
import com.n1netails.n1netails.api.model.entity.N1neTokenEntity;
import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.CreateTokenRequest;
import com.n1netails.n1netails.api.model.response.N1neTokenResponse;
import com.n1netails.n1netails.api.repository.N1neTokenRepository;
import com.n1netails.n1netails.api.repository.OrganizationRepository;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.service.N1neTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("n1neTokenService")
public class N1neTokenServiceImpl implements N1neTokenService {

    public static final String TOKEN_DOES_NOT_EXIST = "Token does not exist: ";
    public static final String USER_DOES_NOT_EXIST = "User does not exist: ";

    private final N1neTokenRepository n1neTokenRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    @Override
    public N1neTokenResponse create(CreateTokenRequest createTokenRequest) {
        N1neTokenEntity n1neTokenEntity = new N1neTokenEntity();
        UsersEntity user = this.userRepository.findById(createTokenRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(USER_DOES_NOT_EXIST + createTokenRequest.getUserId()));
        OrganizationEntity organization = this.organizationRepository.findById(createTokenRequest.getOrganizationId())
                .orElseThrow(() -> new IllegalArgumentException("Organization does not exist"));

        // ensure user is part of organization
        if (user.getOrganizations().contains(organization)) {
            n1neTokenEntity.setUser(user);
            n1neTokenEntity.setCreatedAt(Instant.now());
            n1neTokenEntity.setExpiresAt(createTokenRequest.getExpiresAt());
            n1neTokenEntity.setName(createTokenRequest.getName());
            n1neTokenEntity.setToken(UUID.randomUUID());
            n1neTokenEntity.setOrganization(organization);
            log.info("Saving new token");
            n1neTokenEntity = this.n1neTokenRepository.save(n1neTokenEntity);
            log.info("Generating token response");
            return generateN1neTokenResponse(n1neTokenEntity);
        } else {
            throw new IllegalArgumentException("User is not part of requested organization");
        }
    }

    @Override
    public N1neTokenResponse getById(Long id) {
        N1neTokenEntity n1neTokenEntity = this.n1neTokenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(TOKEN_DOES_NOT_EXIST + id));
        return generateN1neTokenResponse(n1neTokenEntity);
    }

    @Override
    public List<N1neTokenResponse> getAllTokens() {
        // This method is for Super Admins to get all tokens.
        // The existing TODOs are addressed by the controller's authorization logic.
        // Token value is returned as per N1neTokenResponse structure.
        List<N1neTokenEntity> n1neTokenEntities = this.n1neTokenRepository.findAll();
        return n1neTokenEntities.stream()
                .map(N1neTokenServiceImpl::generateN1neTokenResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<N1neTokenResponse> getAllTokensForOrganizations(Set<Long> organizationIds) {
        // This method is for Organization Admins.
        // It fetches tokens belonging to the specified organizations.
        if (organizationIds == null || organizationIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<N1neTokenEntity> n1neTokenEntities = this.n1neTokenRepository.findByOrganization_IdIn(organizationIds);
        return n1neTokenEntities.stream()
                .map(N1neTokenServiceImpl::generateN1neTokenResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<N1neTokenResponse> getAllByUserId(Long userId) {
        List<N1neTokenEntity> n1neTokenEntities = this.n1neTokenRepository.findByUserId(userId);
        List<N1neTokenResponse> n1neTokenResponseList = new ArrayList<>();
        n1neTokenEntities.forEach(entity -> {
            n1neTokenResponseList.add(generateN1neTokenResponse(entity));
        });
        return n1neTokenResponseList;
    }

    @Override
    public void revoke(Long id) {
        N1neTokenEntity n1neTokenEntity = this.n1neTokenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(TOKEN_DOES_NOT_EXIST + id));
        n1neTokenEntity.setRevoked(true);
        this.n1neTokenRepository.save(n1neTokenEntity);
    }

    @Override
    public void enable(Long id) {
        N1neTokenEntity n1neTokenEntity = this.n1neTokenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(TOKEN_DOES_NOT_EXIST + id));
        n1neTokenEntity.setRevoked(false);
        this.n1neTokenRepository.save(n1neTokenEntity);
    }

    @Override
    public void delete(Long id) {
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

    @Override
    public void setLastUsedAt(String n1neToken) throws N1neTokenNotFoundException {
        UUID token = UUID.fromString(n1neToken);
        N1neTokenEntity n1neTokenEntity = this.n1neTokenRepository.findByToken(token)
                .orElseThrow(() -> new N1neTokenNotFoundException("Unable to set token last used at. N1ne token not found."));
        n1neTokenEntity.setLastUsedAt(Instant.now());
        this.n1neTokenRepository.save(n1neTokenEntity);
    }

    private static N1neTokenResponse generateN1neTokenResponse(N1neTokenEntity n1neTokenEntity) {
        N1neTokenResponse n1neTokenResponse = new N1neTokenResponse();
        n1neTokenResponse.setId(n1neTokenEntity.getId());
        n1neTokenResponse.setToken(n1neTokenEntity.getToken()); // Token value is included
        n1neTokenResponse.setCreatedAt(n1neTokenEntity.getCreatedAt());
        n1neTokenResponse.setRevoked(n1neTokenEntity.isRevoked());
        n1neTokenResponse.setExpiresAt(n1neTokenEntity.getExpiresAt());
        n1neTokenResponse.setName(n1neTokenEntity.getName());
        if (n1neTokenEntity.getUser() != null) {
            n1neTokenResponse.setUserId(n1neTokenEntity.getUser().getId());
        }
        if (n1neTokenEntity.getOrganization() != null) {
            n1neTokenResponse.setOrganizationId(n1neTokenEntity.getOrganization().getId());
        }
        n1neTokenResponse.setLastUsedAt(n1neTokenEntity.getLastUsedAt());
        return n1neTokenResponse;
    }
}

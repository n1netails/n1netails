package com.n1netails.n1netails.api.service.impl;

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
    public List<N1neTokenResponse> getAll() {
        // TODO MAKE SURE ONLY THE ORGANIZATION ADMIN CAN GET ALL TOKENS IN THEIR ORGANIZATION
        // TODO DO NOT RETURN THE TOKEN VALUE HERE
        List<N1neTokenEntity> n1neTokenEntities = this.n1neTokenRepository.findAll();
        List<N1neTokenResponse> n1neTokenResponseList = new ArrayList<>();
        n1neTokenEntities.forEach(entity -> {
            n1neTokenResponseList.add(generateN1neTokenResponse(entity));
        });
        return n1neTokenResponseList;
    }

    @Override
    public List<N1neTokenResponse> getAllByUserId(Long userId) {
        // TODO MAKE SURE ONLY THE USER CAN GET THEIR OWN TOKENS
        List<N1neTokenEntity> n1neTokenEntities = this.n1neTokenRepository.findByUserId(userId);
        List<N1neTokenResponse> n1neTokenResponseList = new ArrayList<>();
        n1neTokenEntities.forEach(entity -> {
            n1neTokenResponseList.add(generateN1neTokenResponse(entity));
        });
        return n1neTokenResponseList;
    }

    @Override
    public void revoke(Long id) {
        // TODO MAKE SURE ONLY OWNER AND ORGANIZATION ADMINS CAN REVOKE A TOKEN
        N1neTokenEntity n1neTokenEntity = this.n1neTokenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(TOKEN_DOES_NOT_EXIST + id));
        n1neTokenEntity.setRevoked(true);
        this.n1neTokenRepository.save(n1neTokenEntity);
    }

    @Override
    public void enable(Long id) {
        // TODO MAKE SURE ONLY OWNER AND ORGANIZATION ADMINS CAN ENABLE A TOKEN
        N1neTokenEntity n1neTokenEntity = this.n1neTokenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(TOKEN_DOES_NOT_EXIST + id));
        n1neTokenEntity.setRevoked(false);
        this.n1neTokenRepository.save(n1neTokenEntity);
    }

    @Override
    public void delete(Long id) {
        // TODO MAKE SURE ONLY OWNER CAN DELETE A TOKEN
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
        n1neTokenResponse.setOrganizationId(n1neTokenEntity.getOrganization().getId());
        if (n1neTokenEntity.getOrganization() != null)
            n1neTokenResponse.setOrganizationId(n1neTokenEntity.getOrganization().getId());
        return n1neTokenResponse;
    }
}

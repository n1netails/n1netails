package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.N1neTokenGenerateException;
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
import com.n1netails.n1netails.api.util.N1TokenGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    public N1neTokenResponse create(CreateTokenRequest createTokenRequest) throws N1neTokenGenerateException {
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

            // done replace with n1_token_hash and return token to user but do not have token value in database
//            n1neTokenEntity.setToken(UUID.randomUUID());

            N1TokenGenerator.N1TokenResult n1TokenResult = N1TokenGenerator.generateToken();
            String token = n1TokenResult.getTokenPlain();
            byte[] tokenHash = n1TokenResult.getTokenHash();

            n1neTokenEntity.setN1TokenHash(tokenHash);


            n1neTokenEntity.setOrganization(organization);
            log.info("Saving new token");
            n1neTokenEntity = this.n1neTokenRepository.save(n1neTokenEntity);
            log.info("Generating token response");
            return generateN1neTokenCreateResponse(n1neTokenEntity, token);
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
    public Page<N1neTokenResponse> getAllByUserId(Long userId, com.n1netails.n1netails.api.model.request.PageRequest pageRequest) {
        Sort sort = Sort.by(pageRequest.getSortDirection(), pageRequest.getSortBy());
        PageRequest springPageRequest = PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
        Page<N1neTokenEntity> n1neTokenEntitiesPage = this.n1neTokenRepository.findByUserId(userId, springPageRequest);
        List<N1neTokenResponse> n1neTokenResponseList = n1neTokenEntitiesPage.getContent().stream()
                .map(N1neTokenServiceImpl::generateN1neTokenResponse)
                .toList();
        return new PageImpl<>(n1neTokenResponseList, springPageRequest, n1neTokenEntitiesPage.getTotalElements());
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
    public boolean validateToken(String n1neToken) throws N1neTokenGenerateException {
        // todo remove old token and instead hash incoming token to check if it matches token hash in db
//        log.info("validating token");
//        UUID token = UUID.fromString(n1neToken);
//        log.info("attempting to locate token");
//        Optional<N1neTokenEntity> optionalN1neTokenEntity = this.n1neTokenRepository.findByToken(token);

        log.info("attempting to locate token hash");
        byte[] tokenHash = N1TokenGenerator.sha256(n1neToken);
        Optional<N1neTokenEntity> optionalN1neTokenEntity = this.n1neTokenRepository.findByN1TokenHash(tokenHash);

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
    public void setLastUsedAt(String n1neToken) throws N1neTokenNotFoundException, N1neTokenGenerateException {
        // todo remove old token and instead hash incoming token to check if it matches token hash in db
//        UUID token = UUID.fromString(n1neToken);
//        N1neTokenEntity n1neTokenEntity = this.n1neTokenRepository.findByToken(token)
//                .orElseThrow(() -> new N1neTokenNotFoundException("Unable to set token last used at. N1ne token not found."));

        byte[] tokenHash = N1TokenGenerator.sha256(n1neToken);
        N1neTokenEntity n1neTokenEntity = this.n1neTokenRepository.findByN1TokenHash(tokenHash)
                .orElseThrow(() -> new N1neTokenNotFoundException("Unable to set token last used at. N1ne token not found."));


        n1neTokenEntity.setLastUsedAt(Instant.now());
        this.n1neTokenRepository.save(n1neTokenEntity);
    }

    private static N1neTokenResponse generateN1neTokenResponse(N1neTokenEntity n1neTokenEntity) {
        N1neTokenResponse n1neTokenResponse = new N1neTokenResponse();
        n1neTokenResponse.setId(n1neTokenEntity.getId());
//        n1neTokenResponse.setToken(n1neTokenEntity.getToken()); // Token value is included
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


    private static N1neTokenResponse generateN1neTokenCreateResponse(N1neTokenEntity n1neTokenEntity, String token) {
        N1neTokenResponse n1neTokenResponse = new N1neTokenResponse();
        n1neTokenResponse.setId(n1neTokenEntity.getId());
//        n1neTokenResponse.setToken(n1neTokenEntity.getToken()); // Token value is included

        n1neTokenResponse.setN1Token(token);

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

package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.N1neTokenNotFoundException;
import com.n1netails.n1netails.api.model.request.CreateTokenRequest;
import com.n1netails.n1netails.api.model.response.N1neTokenResponse;

import java.util.List;
import java.util.Set;

public interface N1neTokenService {

    N1neTokenResponse create(CreateTokenRequest createTokenRequest);
    N1neTokenResponse getById(Long id);
    // List<N1neTokenResponse> getAll(); // Replaced by getAllTokens and getAllTokensForOrganizations
    List<N1neTokenResponse> getAllTokens(); // For Super Admins
    List<N1neTokenResponse> getAllTokensForOrganizations(Set<Long> organizationIds); // For Org Admins
    List<N1neTokenResponse> getAllByUserId(Long userId);
    void revoke(Long id);
    void enable(Long id);
    void delete(Long id);
    boolean validateToken(String n1neToken);
    void setLastUsedAt(String n1neToken) throws N1neTokenNotFoundException;
}

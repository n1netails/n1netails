package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.model.request.CreateTokenRequest;
import com.n1netails.n1netails.api.model.response.N1neTokenResponse;

import java.util.List;

public interface N1neTokenService {

    N1neTokenResponse create(CreateTokenRequest createTokenRequest);
    N1neTokenResponse getById(Long id);
    List<N1neTokenResponse> getAll();
    void revoke(Long id);
    void delete(Long id);
}

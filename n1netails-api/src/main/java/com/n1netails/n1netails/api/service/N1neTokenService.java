package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.N1neTokenGenerateException;
import com.n1netails.n1netails.api.exception.type.N1neTokenNotFoundException;
import com.n1netails.n1netails.api.model.request.CreateTokenRequest;
import com.n1netails.n1netails.api.model.request.PageRequest;
import com.n1netails.n1netails.api.model.response.N1neTokenResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface N1neTokenService {

    N1neTokenResponse create(CreateTokenRequest createTokenRequest) throws N1neTokenGenerateException;
    N1neTokenResponse getById(Long id);
    Page<N1neTokenResponse> getAllByUserId(Long userId, PageRequest pageRequest);
    void revoke(Long id);
    void enable(Long id);
    void delete(Long id);
    boolean validateToken(String n1neToken) throws N1neTokenGenerateException;
    void setLastUsedAt(String n1neToken) throws N1neTokenNotFoundException, N1neTokenGenerateException;
}

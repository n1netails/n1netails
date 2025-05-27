package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.model.request.KudaTailRequest;

public interface AlertService {

    void createTail(String token, KudaTailRequest request);
}

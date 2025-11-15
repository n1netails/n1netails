package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.N1neTokenGenerateException;
import com.n1netails.n1netails.api.exception.type.OrganizationNotFoundException;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;

public interface AlertService {

    void createTail(String token, KudaTailRequest request) throws N1neTokenGenerateException, N1neTokenGenerateException;
    void createManualTail(Long organizationId, UsersEntity usersEntity, KudaTailRequest request) throws OrganizationNotFoundException;
}

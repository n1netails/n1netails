package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.UserNotFoundException;


public interface AuthorizationService {

    boolean isSelf(String authorizationHeader, Long userId) throws UserNotFoundException;
    boolean isOwnerOrOrganizationAdmin(String authorizationHeader, Long userId, Long organizationId) throws UserNotFoundException;
}

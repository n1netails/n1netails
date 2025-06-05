package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.UserPrincipal;

public interface AuthorizationService {

    UserPrincipal getCurrentUserPrincipal(String authorizationHeader) throws UserNotFoundException;

    boolean isOrganizationAdmin(UserPrincipal principal, Long organizationId);

    boolean isSuperAdmin(UserPrincipal principal);

    boolean belongsToOrganization(UserPrincipal principal, Long organizationId);

    boolean isTailOwner(UserPrincipal principal, Long tailUserId);

    boolean isSelf(String authorizationHeader, Long userId) throws UserNotFoundException;

    // Overloaded isSelf method for convenience when UserPrincipal is already available
    boolean isSelf(UserPrincipal principal, Long userIdToCheck);

    boolean isOwnerOrOrganizationAdmin(UserPrincipal principal, Long ownerUserId, Long organizationId);
}

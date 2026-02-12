package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.UserPrincipal;



/**
 * Service responsible for authentication context resolution and
 * authorization checks across the system.
 *
 * <p>
 * Provides operations to get the current authenticated user
 * and to evaluate access permissions based on user roles,
 * organization membership, ownership, and assignment rules.
 * </p>
 */
public interface AuthorizationService {

    /**
     * Resolves the currently authenticated user principal from an
     * authorization header.
     *
     * <p>
     * The authorization header is expected to contain a valid token
     * that can be decoded to identify the authenticated user.
     * </p>
     *
     * @param authorizationHeader the authorization header containing the token
     * @return the resolved user principal
     * @throws UserNotFoundException if no user can be resolved from the token
     */
    UserPrincipal getCurrentUserPrincipal(String authorizationHeader) throws UserNotFoundException;

    /**
     * Determines whether the given user is an administrator of the
     * specified organization.
     *
     * @param principal       the authenticated user principal
     * @param organizationId  the identifier of the organization
     * @return {@code true} if the user is an administrator of the organization;
     *         {@code false} otherwise
     */
    boolean isOrganizationAdmin(UserPrincipal principal, Long organizationId);

    /**
     * Determines whether the given user has super administrator privileges.
     *
     * @param principal the authenticated user principal
     * @return {@code true} if the user is a super administrator;
     *         {@code false} otherwise
     */
    boolean isSuperAdmin(UserPrincipal principal);


    /**
     * Determines whether the given user belongs to the specified organization.
     *
     * @param principal      the authenticated user principal
     * @param organizationId the identifier of the organization
     * @return {@code true} if the user belongs to the organization;
     *         {@code false} otherwise
     */
    boolean belongsToOrganization(UserPrincipal principal, Long organizationId);

    /**
     * Determines whether the given user is the owner of a tail.
     *
     * @param principal  the authenticated user principal
     * @param tailUserId the identifier of the user assigned to the tail
     * @return {@code true} if the user is the owner of the tail;
     *         {@code false} otherwise
     */
    boolean isTailOwner(UserPrincipal principal, Long tailUserId);

    /**
     * Determines whether the given user represents the same identity
     * as the provided user identifier.
     *
     * @param principal      the authenticated user principal
     * @param userIdToCheck  the user identifier to compare against
     * @return {@code true} if the identifiers match;
     *         {@code false} otherwise
     */
    boolean isSelf(UserPrincipal principal, Long userIdToCheck);

    /**
     * Determines whether the given user is either the owner of a resource
     * or an administrator of the specified organization.
     *
     * @param principal       the authenticated user principal
     * @param ownerUserId     the identifier of the resource owner
     * @param organizationId the identifier of the organization
     * @return {@code true} if the user is the owner or an organization administrator;
     *         {@code false} otherwise
     */
    boolean isOwnerOrOrganizationAdmin(UserPrincipal principal, Long ownerUserId, Long organizationId);


    /**
     * Determines whether the given user is authorized to access a tail.
     *
     * <p>
     * Authorization may be granted if the user is directly assigned
     * to the tail, is an administrator of the associated organization,
     * or has super administrator privileges.
     * </p>
     *
     * @param principal the authenticated user principal
     * @param tailId    the identifier of the tail
     * @return {@code true} if the user is authorized to access the tail;
     *         {@code false} otherwise
     */
    boolean isUserAssignedToTail(UserPrincipal principal, Long tailId);

    /**
     * Determines whether the given user is the owner of the specified token.
     *
     * @param principal the authenticated user principal
     * @param tokenId   the identifier of the token
     * @return {@code true} if the user owns the token;
     *         {@code false} otherwise
     */
    boolean isN1neTokenOwner(UserPrincipal principal, Long tokenId);
}

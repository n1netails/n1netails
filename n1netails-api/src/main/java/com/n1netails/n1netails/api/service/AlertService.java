package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.N1neTokenGenerateException;
import com.n1netails.n1netails.api.exception.type.OrganizationNotFoundException;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;

/**
 * Service responsible for creating and managing alert tails within the system.
 *
 * <p>
 * This service provides operations to create system-generated and manual
 * alert tails associated with an organization and user context.
 * </p>
 *
 * <p>
 * Implementations handle default alert attributes (level, type, status),
 * metadata mapping, and notification triggering as part of the alert lifecycle.
 * </p>
 */
public interface AlertService {

    /**
     * Creates a new alert tail using an authentication token.
     *
     * <p>
     * The token is used to resolve the associated user and organization.
     * Upon successful creation, a notification is triggered for the alert.
     * </p>
     *
     * @param token   the authentication token identifying the user and organization
     * @param request the alert tail details to be created
     * @throws N1neTokenGenerateException if the token cannot be processed or resolved
     */
    void createTail(String token, KudaTailRequest request) throws N1neTokenGenerateException;

    /**
     * Creates a new alert tail manually for a given organization and user.
     *
     * <p>
     * This operation does not trigger notifications automatically.
     * </p>
     *
     * @param organizationId the identifier of the organization
     * @param usersEntity    the user associated with the alert
     * @param request        the alert tail details to be created
     * @throws OrganizationNotFoundException if the organization does not exist
     */
    void createManualTail(Long organizationId, UsersEntity usersEntity, KudaTailRequest request) throws OrganizationNotFoundException;


    /**
     * Creates a new alert tail manually and triggers a notification.
     *
     * <p>
     * This variant allows associating the alert with a specific token
     * identifier for notification tracking purposes.
     * </p>
     *
     * @param organizationId the identifier of the organization
     * @param usersEntity    the user associated with the alert
     * @param request        the alert tail details to be created
     * @param n1neTokenId    the identifier of the token used for notification dispatch
     * @throws OrganizationNotFoundException if the organization does not exist
     */
    void createManualTail(Long organizationId, UsersEntity usersEntity, KudaTailRequest request, Long n1neTokenId) throws OrganizationNotFoundException;
}

package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.model.request.OrganizationRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

/**
 * Service responsible for managing organizations and organization membership.
 *
 * <p>This service is responsible for:
 * <ul>
 *     <li>Creating and retrieving organizations</li>
 *     <li>Managing user membership within organizations</li>
 *     <li>Enforcing organization-specific business rules</li>
 * </ul>
 *
 * <h3>Special Rules</h3>
 * <ul>
 *     <li>The default organization <b>"n1netails"</b> has special constraints:
 *         <ul>
 *             <li>A user cannot be added to "n1netails" if they already belong to another organization.</li>
 *             <li>When a user is added to a non-"n1netails" organization, they are automatically removed from "n1netails".</li>
 *         </ul>
 *     </li>
 *     <li>Admin-only operations require the acting user to be a member of the target organization.</li>
 * </ul>
 */
public interface OrganizationService {

    /**
     * Creates a new organization.
     *
     * @param organizationRequest the organization creation request containing
     *                            name, description, and address
     * @return the newly created organization entity
     * @throws RuntimeException if an organization with the same name already exists
     */
    OrganizationEntity createOrganization(OrganizationRequest organizationRequest);

    /**
     * Retrieves all organizations. The list may be empty if no organizations exist
     *
     * @return a list of all organizations; never {@code null}
     */
    List<OrganizationEntity> getAllOrganizations();

    /**
     * Adds a user to an organization.
     *
     * <p>This method enforces special rules for the "n1netails" organization
     * and prevents duplicate membership.</p>
     *
     * @param organizationId the ID of the target organization
     * @param userId         the ID of the user to add
     * @throws EntityNotFoundException if the organization or user does not exist
     * @throws IllegalStateException   if the user is already a member of the organization
     *                                 or violates organization membership rules
     */
    void addUserToOrganization(Long organizationId, Long userId);

    /**
     * Removes a user from an organization.
     *
     * @param organizationId the ID of the organization
     * @param userId         the ID of the user to remove
     * @throws EntityNotFoundException if the organization or user does not exist
     * @throws RuntimeException        if the user is not a member of the organization
     */
    void removeUserFromOrganization(Long organizationId, Long userId);


    /**
     * Adds a target user to an organization managed by the requesting admin user.
     *
     * <p>The admin user must be a member of the organization in order to perform
     * this operation.</p>
     *
     * @param organizationId the ID of the organization
     * @param targetUserId   the ID of the user to add
     * @param adminUserEmail the email of the admin performing the action
     * @throws EntityNotFoundException if the organization, admin user,
     *                                 or target user does not exist
     * @throws AccessDeniedException   if the admin user is not a member of the organization
     * @throws IllegalStateException   if the target user is already a member
     */
    void addMemberToMyOrganization(Long organizationId, Long targetUserId, String adminUserEmail);

    /**
     * Removes a target user from an organization managed by the requesting admin user.
     *
     * <p>The admin user must be a member of the organization in order to perform
     * this operation.</p>
     *
     * @param organizationId the ID of the organization
     * @param targetUserId   the ID of the user to remove
     * @param adminUserEmail the email of the admin performing the action
     *
     * @throws EntityNotFoundException if the organization, admin user,
     *                                                  or target user does not exist
     * @throws AccessDeniedException if the admin user is not a member of the organization
     * @throws RuntimeException if the target user is not a member of the organization
     */
    void removeMemberFromMyOrganization(Long organizationId, Long targetUserId, String adminUserEmail);
}

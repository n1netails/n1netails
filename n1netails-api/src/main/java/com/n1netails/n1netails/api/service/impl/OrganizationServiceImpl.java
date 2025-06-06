package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.OrganizationRequest;
import com.n1netails.n1netails.api.repository.OrganizationRepository;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.service.OrganizationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("organizationService")
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    private static final String N1NETAILS_ORG_NAME = "n1netails";

    @Override
    public OrganizationEntity createOrganization(OrganizationRequest organizationRequest) {
        // Check if organization with the same name already exists
        if (organizationRepository.findByName(organizationRequest.getName()).isPresent()) {
            // Consider creating a specific exception for this
            throw new RuntimeException("Organization with name '" + organizationRequest.getName() + "' already exists.");
        }

        OrganizationEntity organization = new OrganizationEntity();
        organization.setName(organizationRequest.getName());
        organization.setDescription(organizationRequest.getDescription());
        organization.setAddress(organizationRequest.getAddress());
        organization.setCreatedAt(new Date());
        organization.setUpdatedAt(new Date());
        // Users set is initialized by Lombok's @NoArgsConstructor or directly in the entity

        return organizationRepository.save(organization);
    }

    @Override
    public List<OrganizationEntity> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    @Override
    public void addUserToOrganization(Long organizationId, Long userId) {
        OrganizationEntity targetOrganization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with ID: " + organizationId));

        UsersEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        // It's important that 'user.getOrganizations()' is populated.
        // findById should work within a @Transactional service method.

        OrganizationEntity n1netailsOrgInstance = organizationRepository.findByName(N1NETAILS_ORG_NAME)
                .orElseThrow(() -> new EntityNotFoundException("'" + N1NETAILS_ORG_NAME + "' organization not found. Critical setup issue."));
        // If n1netailsOrgInstance is the target, it's fine. Helper handles it.

        prepareUserForOrganizationAssignment(user, targetOrganization, n1netailsOrgInstance);

        if (targetOrganization.getUsers().contains(user)) {
            throw new IllegalStateException("User " + user.getEmail() + " is already a member of organization " + targetOrganization.getName());
        }
        targetOrganization.getUsers().add(user);
        organizationRepository.save(targetOrganization);
    }

    @Override
    public void removeUserFromOrganization(Long organizationId, Long userId) {
        OrganizationEntity organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with ID: " + organizationId));

        UsersEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        if (organization.getUsers().contains(user)) {
            organization.getUsers().remove(user);
            organizationRepository.save(organization);
        } else {
            // User not in organization, maybe log or throw specific exception
            throw new RuntimeException("User with ID " + userId + " is not a member of organization " + organizationId);
        }

        // Note: The logic for re-assigning to "n1netails" org if this was the user's last
        // non-n1netails org is NOT implemented here as per subtask instructions.
    }

    @Override
    public void addMemberToMyOrganization(Long organizationId, Long targetUserId, String adminUserEmail) {
        OrganizationEntity organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with ID: " + organizationId));

        UsersEntity targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("Target user not found with ID: " + targetUserId));

        UsersEntity adminUser = userRepository.findUserByEmail(adminUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found with email: " + adminUserEmail));

        // Admin Authorization Check
        if (!organization.getUsers().contains(adminUser)) { // Assumes adminUser.organizations is loaded or accessible
            throw new AccessDeniedException("Admin user " + adminUserEmail + " is not a member of organization " + organizationId);
        }

        // Ensure targetUser's organizations are available for the helper method
        // Re-fetching targetUser or ensuring it's fully loaded might be needed if it wasn't the first entity loaded.
        // However, findById in a @Transactional method should generally allow access to LAZY collections.

        OrganizationEntity n1netailsOrgInstance = organizationRepository.findByName(N1NETAILS_ORG_NAME)
                .orElseThrow(() -> new EntityNotFoundException("'" + N1NETAILS_ORG_NAME + "' organization not found. Critical setup issue."));

        prepareUserForOrganizationAssignment(targetUser, organization, n1netailsOrgInstance);

        if(organization.getUsers().contains(targetUser)){
            throw new IllegalStateException("Target user " + targetUser.getEmail() + " is already a member of organization " + organization.getName());
        }

        organization.getUsers().add(targetUser);
        organizationRepository.save(organization);
    }

    @Override
    public void removeMemberFromMyOrganization(Long organizationId, Long targetUserId, String adminUserEmail) {
        OrganizationEntity organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with ID: " + organizationId));

        UsersEntity targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("Target user not found with ID: " + targetUserId));

        UsersEntity adminUser = userRepository.findUserByEmail(adminUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found with email: " + adminUserEmail));

        // Admin Authorization Check
        if (!organization.getUsers().contains(adminUser)) {
            throw new AccessDeniedException("Admin user " + adminUserEmail + " is not a member of organization " + organizationId);
        }

        if (organization.getUsers().contains(targetUser)) {
            organization.getUsers().remove(targetUser);
            organizationRepository.save(organization);
        } else {
            throw new RuntimeException("Target user with ID " + targetUserId + " is not a member of organization " + organizationId);
        }
    }

    private void prepareUserForOrganizationAssignment(UsersEntity user, OrganizationEntity targetOrganization, OrganizationEntity n1netailsOrgInstance) {
        if (N1NETAILS_ORG_NAME.equalsIgnoreCase(targetOrganization.getName())) {
            boolean inOtherOrgs = user.getOrganizations().stream()
                    .anyMatch(org -> !N1NETAILS_ORG_NAME.equalsIgnoreCase(org.getName()));
            if (inOtherOrgs) {
                throw new IllegalStateException("User " + user.getEmail() + " is already a member of other organizations and cannot be added to '" + N1NETAILS_ORG_NAME + "'.");
            }
        } else { // Target is not "n1netails"
            if (n1netailsOrgInstance != null && user.getOrganizations().contains(n1netailsOrgInstance)) {
                n1netailsOrgInstance.getUsers().remove(user);
                // user.getOrganizations().remove(n1netailsOrgInstance);
                organizationRepository.save(n1netailsOrgInstance);
            }
        }
    }
}

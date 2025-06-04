package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.OrganizationRequestDto;
import com.n1netails.n1netails.api.repository.OrganizationRepository;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.service.OrganizationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    @Override
    public OrganizationEntity createOrganization(OrganizationRequestDto organizationDto) {
        // Check if organization with the same name already exists
        if (organizationRepository.findByName(organizationDto.getName()).isPresent()) {
            // Consider creating a specific exception for this
            throw new RuntimeException("Organization with name '" + organizationDto.getName() + "' already exists.");
        }

        OrganizationEntity organization = new OrganizationEntity();
        organization.setName(organizationDto.getName());
        organization.setDescription(organizationDto.getDescription());
        organization.setAddress(organizationDto.getAddress());
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

        // Crucial "n1netails" logic
        if (!"n1netails".equalsIgnoreCase(targetOrganization.getName())) {
            Optional<OrganizationEntity> n1netailsOrgOpt = organizationRepository.findByName("n1netails");
            if (n1netailsOrgOpt.isPresent()) {
                OrganizationEntity n1netailsOrg = n1netailsOrgOpt.get();
                if (n1netailsOrg.getUsers().contains(user)) {
                    n1netailsOrg.getUsers().remove(user);
                    organizationRepository.save(n1netailsOrg);
                }
            } else {
                // This case should ideally not happen if Liquibase scripts ran correctly.
                // Consider logging a warning or throwing a specific exception.
                throw new EntityNotFoundException("'n1netails' organization not found, cannot perform user transfer.");
            }
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
        if (!organization.getUsers().contains(adminUser)) {
            throw new AccessDeniedException("Admin user " + adminUserEmail + " is not a member of organization " + organizationId);
        }

        // "n1netails" logic (same as for Super Admin)
        if (!"n1netails".equalsIgnoreCase(organization.getName())) {
            Optional<OrganizationEntity> n1netailsOrgOpt = organizationRepository.findByName("n1netails");
            if (n1netailsOrgOpt.isPresent()) {
                OrganizationEntity n1netailsOrg = n1netailsOrgOpt.get();
                if (n1netailsOrg.getUsers().contains(targetUser)) {
                    n1netailsOrg.getUsers().remove(targetUser);
                    organizationRepository.save(n1netailsOrg);
                }
            } else {
                throw new EntityNotFoundException("'n1netails' organization not found, cannot perform user transfer.");
            }
        }

        if(organization.getUsers().contains(targetUser)){
            throw new RuntimeException("Target user with ID " + targetUserId + " is already a member of organization " + organizationId);
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
}

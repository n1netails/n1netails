package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.model.request.OrganizationRequest;

import java.util.List;

public interface OrganizationService {
    OrganizationEntity createOrganization(OrganizationRequest organizationRequest);
    List<OrganizationEntity> getAllOrganizations();
    void addUserToOrganization(Long organizationId, Long userId);
    void removeUserFromOrganization(Long organizationId, Long userId);
    void addMemberToMyOrganization(Long organizationId, Long targetUserId, String adminUserEmail);
    void removeMemberFromMyOrganization(Long organizationId, Long targetUserId, String adminUserEmail);
    // TODO Add findById, update, delete methods later
}

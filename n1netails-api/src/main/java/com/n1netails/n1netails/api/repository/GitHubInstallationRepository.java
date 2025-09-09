package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.GitHubInstallationEntity;
import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GitHubInstallationRepository extends JpaRepository<GitHubInstallationEntity, Long> {
    Optional<GitHubInstallationEntity> findByOrganization(OrganizationEntity organization);
}

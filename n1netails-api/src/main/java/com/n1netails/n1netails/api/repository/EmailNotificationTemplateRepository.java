package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.EmailNotificationTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * EmailNotificationTemplateRepository
 */
@Repository
public interface EmailNotificationTemplateRepository
        extends JpaRepository<EmailNotificationTemplateEntity, Long> {

    Optional<EmailNotificationTemplateEntity> findByName(String name);
}

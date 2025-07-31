package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.entity.EmailNotificationTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * EmailNotificationTemplateRepository
 */
@Repository
public interface EmailNotificationTemplateRepository
        extends JpaRepository<EmailNotificationTemplateEntity, String> {
}

package com.n1netails.n1netails.api.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * EmailNotificationTemplateEntity
 */
@Getter
@Setter
@Entity
@Table(name = "email_notification_template", schema = "ntail")
public class EmailNotificationTemplateEntity {
    @Id
    private String id;

    private String subject;
    private String htmlBody;
}

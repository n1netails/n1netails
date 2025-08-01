package com.n1netails.n1netails.api.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_notification_template_seq")
    @SequenceGenerator(name = "email_notification_template_seq", sequenceName = "email_notification_template_seq", allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String name;
    private String subject;
    private String htmlBody;
}

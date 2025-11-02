package com.n1netails.n1netails.api.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_notification_preference")
public class UserNotificationPreferenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_notification_preference_seq")
    @SequenceGenerator(name = "user_notification_preference_seq", sequenceName = "user_notification_preference_seq", allocationSize = 1)
    private Long id;

    private Long userId;

    private String platform;
}

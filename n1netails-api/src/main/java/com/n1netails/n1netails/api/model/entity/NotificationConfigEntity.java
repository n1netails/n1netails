package com.n1netails.n1netails.api.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Data
@Entity
@Table(name = "notification_config")
public class NotificationConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_config_seq")
    @SequenceGenerator(name = "notification_config_seq", sequenceName = "notification_config_seq", allocationSize = 1)
    private Long id;

    private Long tokenId;

    private String platform;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> details;
}

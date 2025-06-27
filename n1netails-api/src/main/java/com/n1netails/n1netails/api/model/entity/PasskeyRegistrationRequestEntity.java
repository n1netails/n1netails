package com.n1netails.n1netails.api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "passkey_registration_requests", indexes = {
    @Index(name = "idx_passkey_reg_requests_created_at", columnList = "createdAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasskeyRegistrationRequestEntity {

    @Id
    @Column(name = "request_id", length = 255) // Stores the challenge
    private String requestId;

    @Column(name = "user_id") // Can be null if user is not yet fully identified
    private Long userId;

    @Column(name = "username")
    private String username; // Store username for linking back if userId is not available yet

    @Lob // For potentially large JSON string
    @Column(name = "registration_options", nullable = false, columnDefinition = "TEXT")
    private String registrationOptions; // JSON serialized PublicKeyCredentialCreationOptions

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}

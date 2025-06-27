package com.n1netails.n1netails.api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp; // Or use @PreUpdate for lastUsedAt

import java.time.OffsetDateTime; // Using OffsetDateTime for timezone awareness

@Entity
@Table(name = "passkey_credentials", indexes = {
    @Index(name = "idx_passkey_credentials_user_id", columnList = "user_id"),
    @Index(name = "idx_passkey_credentials_user_handle", columnList = "userHandle")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasskeyCredentialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UsersEntity user;

    @Column(name = "external_id", nullable = false, unique = true, length = 2048)
    private String externalId; // Base64URL representation of Credential ID

    @Column(name = "public_key_cose", nullable = false, length = 2048)
    private String publicKeyCose; // Base64URL representation of COSE public key

    @Column(name = "count", nullable = false)
    private Long count; // Signature counter

    @Column(name = "aaguid", length = 36)
    private String aaguid; // Authenticator Attestation Globally Unique Identifier

    @Column(name = "user_handle", nullable = false) // User handle used during registration
    private String userHandle;

    @Column(name = "credential_type", length = 255)
    private String credentialType; // e.g., "public-key"

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    // This should be updated on each successful authentication
    @Column(name = "last_used_at", nullable = false)
    private OffsetDateTime lastUsedAt;

    @Column(name = "user_agent", length = 512)
    private String userAgent; // User agent of the client that registered the key

    @Column(name = "friendly_name", length = 255)
    private String friendlyName; // Optional: a name given by the user for the passkey

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        lastUsedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        // lastUsedAt will be updated manually upon successful login
    }
}

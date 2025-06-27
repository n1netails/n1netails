package com.n1netails.n1netails.api.model.entity;

import com.yubico.webauthn.data.AttestationType;
import com.yubico.webauthn.data.AuthenticatorTransport;
import com.yubico.webauthn.data.ByteArray;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "user_authenticators", schema = "ntail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthenticator {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UsersEntity user;

    @Column(name = "name", nullable = false)
    private String name; // User-friendly name for the authenticator

    @Column(name = "rp_id", nullable = false)
    private String rpId; // Relying Party ID

    @Column(name = "credential_id", nullable = false, unique = true)
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] credentialIdBytes; // Stored as raw bytes

    @Transient // Yubico library uses ByteArray
    private ByteArray credentialId;

    @Column(name = "public_key", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] publicKeyBytes; // Stored as raw bytes (COSE format)

    @Transient // Yubico library uses ByteArray
    private ByteArray publicKey;


    @Column(name = "user_handle", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] userHandleBytes;

    @Transient
    private ByteArray userHandle;


    @Enumerated(EnumType.STRING)
    @Column(name = "attestation_type")
    private AttestationType attestationType;

    @Column(name = "aaguid")
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID aaguid;

    @Column(name = "sign_count", nullable = false)
    private long signCount;

    @Column(name = "transports")
    private String transports; // Comma-separated list of AuthenticatorTransport values

    @Column(name = "backup_eligible", columnDefinition = "boolean default false")
    private boolean backupEligible;

    @Column(name = "backup_state", columnDefinition = "boolean default false")
    private boolean backupState;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private Instant createdAt;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    // Helper methods to convert between byte[] and ByteArray
    @PostLoad
    void fillTransient() {
        if (credentialIdBytes != null) {
            this.credentialId = new ByteArray(credentialIdBytes);
        }
        if (publicKeyBytes != null) {
            this.publicKey = new ByteArray(publicKeyBytes);
        }
        if (userHandleBytes != null) {
            this.userHandle = new ByteArray(userHandleBytes);
        }
    }

    @PrePersist
    @PreUpdate
    void fillPersistent() {
        if (credentialId != null) {
            this.credentialIdBytes = credentialId.getBytes();
        }
        if (publicKey != null) {
            this.publicKeyBytes = publicKey.getBytes();
        }
         if (userHandle != null) {
            this.userHandleBytes = userHandle.getBytes();
        }
    }

    public Optional<List<AuthenticatorTransport>> getTransportsEnum() {
        if (transports == null || transports.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(List.of(transports.split(",")).stream()
                .map(AuthenticatorTransport::valueOf)
                .toList());
    }

    public void setTransportsEnum(List<AuthenticatorTransport> transportList) {
        if (transportList == null || transportList.isEmpty()) {
            this.transports = null;
        } else {
            this.transports = transportList.stream()
                    .map(AuthenticatorTransport::getId)
                    .collect(java.util.stream.Collectors.joining(","));
        }
    }
}

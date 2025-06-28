package com.n1netails.n1netails.api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "passkey_credentials", schema = "ntail")
public class PasskeyCredentialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "passkey_credentials_seq")
    @SequenceGenerator(name = "passkey_credentials_seq", sequenceName = "passkey_credentials_seq", allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UsersEntity user;

    @Lob // Large object for byte array
    @Column(nullable = false, columnDefinition = "BYTEA")
    private byte[] credentialId;

    @Lob // Large object for byte array
    @Column(nullable = false, columnDefinition = "BYTEA")
    private byte[] publicKeyCose;

    @Column(nullable = false)
    private long signatureCount;

    @Lob // Large object for byte array
    @Column(nullable = false, columnDefinition = "BYTEA")
    private byte[] userHandle;

    private String attestationType;

    private Date registeredAt;

    private Date lastUsedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "passkey_credential_transports",
            schema = "ntail",
            joinColumns = @JoinColumn(name = "passkey_id") // e.g., "usb", "nfc", "ble", "internal"
    )
    @Column(name = "transport")
    private Set<String> transports;

    private Boolean uvInitialized; // User Verification Initialized

    private Boolean backupEligible;

    private Boolean backupState;

    // Recommended to store, from a W3C Note: https://www.w3.org/TR/webauthn-recovery/
    private String deviceName; // e.g., "Pixel 7 Pro", "YubiKey 5"

    private UUID aaguid; // Authenticator Attestation Globally Unique Identifier

    @Column(length = 2048) // Optional: Store the attestation object for auditing, might be large
    private String attestationObject; // Store as Base64URL encoded string
}

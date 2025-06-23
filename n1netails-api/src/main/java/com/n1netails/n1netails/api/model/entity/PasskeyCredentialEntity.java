package com.n1netails.n1netails.api.model.entity;

import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.UserVerificationRequirement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

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

    @Column(nullable = false, unique = true)
    private String credentialId; // Store as Base64URL encoded string

    @Lob // Large object for byte array
    @Column(nullable = false, columnDefinition = "BYTEA")
    private byte[] publicKeyCose; // Store as raw bytes

    @Column(nullable = false)
    private long signatureCount;

    private String userHandle; // Store as Base64URL encoded string, usually user's id

    private String attestationType;

    private Date registeredAt;

    private Date lastUsedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "passkey_credential_transports", schema = "ntail", joinColumns = @JoinColumn(name = "credential_id"))
    @Column(name = "transport")
    private Set<String> transports; // e.g., "usb", "nfc", "ble", "internal"

    private Boolean uvInitialized; // User Verification Initialized

    private Boolean backupEligible;

    private Boolean backupState;

    // Recommended to store, from a W3C Note: https://www.w3.org/TR/webauthn-recovery/
    private String deviceName; // e.g., "Pixel 7 Pro", "YubiKey 5"

    private String aaguid; // Authenticator Attestation Globally Unique Identifier

    @Column(length = 2048) // Optional: Store the attestation object for auditing, might be large
    private String attestationObject; // Store as Base64URL encoded string

    public PasskeyCredentialEntity(UsersEntity user, String credentialId, byte[] publicKeyCose, long signatureCount, String userHandle) {
        this.user = user;
        this.credentialId = credentialId;
        this.publicKeyCose = publicKeyCose;
        this.signatureCount = signatureCount;
        this.userHandle = userHandle;
        this.registeredAt = new Date();
    }
}

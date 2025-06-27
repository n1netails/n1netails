package com.n1netails.n1netails.api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.yubico.webauthn.data.ByteArray;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", schema = "ntail")
public class UsersEntity {

    @Id
    // Assuming you want to keep the existing ID generation strategy for the primary key.
    // If you want to switch to UUIDs for user IDs, this would need to change.
    // For WebAuthn userHandle, we'll add a new field.
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @SequenceGenerator(name = "users_seq", sequenceName = "users_seq", allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Long id; // This is the primary key, usually an auto-incrementing number or UUID.

    // userId seems to be a custom textual ID. We'll keep it.
    // For WebAuthn's userHandle, it's best to have a stable, non-reassignable, unique identifier.
    // A common practice is to use the primary key (if it's a UUID) or generate a separate UUID for userHandle.
    // Let's add a dedicated userHandle field for WebAuthn. It should be byte[].
    @Column(name = "user_handle", unique = true)
    @JdbcTypeCode(SqlTypes.BINARY) // Store as raw bytes
    private byte[] userHandleBytes;

    @Transient // Yubico library uses ByteArray
    private ByteArray userHandle;


    private String userId; // Existing field, seems like a business identifier
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String profileImageUrl;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date joinDate;
    private String role; // ROLE_USER, ROLE_ADMIN
    private String[] authorities;
    private boolean isActive;
    private boolean isNotLocked;
    private boolean enabled;

    // Many-to-many relationship with Organizations
    @ManyToMany
    @JoinTable(
            name = "user_organizations", schema = "ntail",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "organization_id")
    )
    private Set<OrganizationEntity> organizations = new HashSet<>();

    // One-to-many relationship with UserAuthenticators
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserAuthenticator> authenticators;

    // Helper methods for userHandle
    @PostLoad
    void fillTransientUserHandle() {
        if (userHandleBytes != null) {
            this.userHandle = new ByteArray(userHandleBytes);
        } else {
            // Generate a user handle if it doesn't exist (e.g., for existing users)
            // This should ideally be done upon user creation or first passkey registration.
            // For simplicity, let's use the string representation of the user's primary ID (Long).
            // A UUID would be better for new userHandles.
             // For new users, generate a UUID-based user handle if not set
            if (this.id != null) { // Check if id is populated (i.e., entity is managed or loaded)
                this.userHandle = new ByteArray(this.id.toString().getBytes()); // Example: using string of Long ID
                this.userHandleBytes = this.userHandle.getBytes();
            }
        }
    }

    @PrePersist
    @PreUpdate
    void fillPersistentUserHandle() {
        if (userHandle != null) {
            this.userHandleBytes = userHandle.getBytes();
        } else if (this.id != null && this.userHandleBytes == null) {
            // If userHandle is null but id exists (e.g. new entity or existing without userHandle)
            // and userHandleBytes is also null, generate it.
            this.userHandle = new ByteArray(this.id.toString().getBytes());
            this.userHandleBytes = this.userHandle.getBytes();
        }
    }

    // Convenience method to get user handle for Yubico library
    public ByteArray getWebAuthnUserHandle() {
        if (this.userHandle == null && this.userHandleBytes != null) {
            this.userHandle = new ByteArray(this.userHandleBytes);
        } else if (this.userHandle == null && this.id != null) {
            // Fallback: if still null, generate from ID. This should be set earlier.
            this.userHandle = new ByteArray(this.id.toString().getBytes());
        }
        return this.userHandle;
    }

    // It's good practice to initialize the userHandle when a new UsersEntity is created.
    // This can be done in a constructor or a service method.
    // Example:
    public UsersEntity(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password; // Should be encoded
        this.joinDate = new Date();
        this.isActive = true;
        this.isNotLocked = true;
        this.enabled = true;
        this.role = "ROLE_USER"; // Default role
        // Generate a user handle, preferably a UUID
        // For this example, we'll let PostLoad/PrePersist handle it based on ID,
        // but a UUID generated here would be better:
        // this.userHandle = new ByteArray(UUID.randomUUID().toString().getBytes());
        // this.userHandleBytes = this.userHandle.getBytes();
    }
}

package com.n1netails.n1netails.api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "n1ne_token", schema = "ntail")
public class N1neTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_seq")
    @SequenceGenerator(name = "token_seq", sequenceName = "token_seq", allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(name = "token", nullable = false, unique = true, updatable = false)
    private UUID token;

    @Column(name = "n1_token_hash", nullable = false, unique = true, updatable = false, length = 32)
    private byte[] n1TokenHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UsersEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private OrganizationEntity organization;

    @Column(name = "name")
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    public N1neTokenEntity() {
        this.token = UUID.randomUUID();
        this.createdAt = Instant.now();
    }
}

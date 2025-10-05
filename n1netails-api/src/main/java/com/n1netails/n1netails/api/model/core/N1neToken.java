package com.n1netails.n1netails.api.model.core;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class N1neToken {
    private String n1Token;
    private Instant createdAt;
    private Instant expiresAt;
    private boolean revoked;

    @Override
    public String toString() {
        return "N1neToken{createdAt=" + createdAt + ", expiresAt=" + expiresAt + ", revoked=" + revoked + "}";
    }
}

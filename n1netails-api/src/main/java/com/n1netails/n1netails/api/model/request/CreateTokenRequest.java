package com.n1netails.n1netails.api.model.request;

import com.n1netails.n1netails.api.model.core.N1neToken;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CreateTokenRequest {

    private Long userId;
    private Long organizationId;
    private String name;
    private Instant expiresAt;
}

package com.n1netails.n1netails.api.model.response;

import com.n1netails.n1netails.api.model.core.N1neToken;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class N1neTokenResponse extends N1neToken {
    private Long id;
    private Long userId;
    private Long organizationId;
    private Instant lastUsedAt;
}

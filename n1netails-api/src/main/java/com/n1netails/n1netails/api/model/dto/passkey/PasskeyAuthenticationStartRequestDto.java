package com.n1netails.n1netails.api.model.dto.passkey;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasskeyAuthenticationStartRequestDto {
    private String username; // Optional: if the user is already known
    private String domain;   // The relying party's domain
}

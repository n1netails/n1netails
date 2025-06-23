package com.n1netails.n1netails.api.model.dto.passkey;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasskeyRegistrationStartRequestDto {
    private String username; // User's username to associate the passkey with
    private String domain; // The relying party's domain
}

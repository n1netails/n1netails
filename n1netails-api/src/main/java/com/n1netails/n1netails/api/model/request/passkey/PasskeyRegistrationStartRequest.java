package com.n1netails.n1netails.api.model.request.passkey;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class PasskeyRegistrationStartRequest {
    @NotBlank(message = "Username cannot be blank")
    private String username;
    private String displayName; // Optional: user's display name for the passkey
}

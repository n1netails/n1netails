package com.n1netails.n1netails.api.model.request.passkey;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class PasskeyRegistrationFinishRequest {
    @NotBlank(message = "Registration ID cannot be blank")
    private String registrationId; // Corresponds to the challenge / request_id

    @NotBlank(message = "Credential data cannot be blank")
    private String credential; // JSON string of PublicKeyCredential from navigator.credentials.create()
}

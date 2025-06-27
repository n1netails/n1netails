package com.n1netails.n1netails.api.model.request.passkey;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class PasskeyLoginFinishRequest {
    @NotBlank(message = "Assertion ID cannot be blank")
    private String assertionId; // Corresponds to the challenge / request_id

    @NotBlank(message = "Credential data cannot be blank")
    private String credential; // JSON string of PublicKeyCredential from navigator.credentials.get()
}

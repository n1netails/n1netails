package com.n1netails.n1netails.api.model.response.passkey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasskeyRegistrationStartResponse {
    private String registrationId; // The challenge, to be stored by the frontend and sent back
    private String options; // JSON string of PublicKeyCredentialCreationOptions
}

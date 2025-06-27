package com.n1netails.n1netails.api.model.request.passkey;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Email;

@Data
public class FinishRegistrationRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String attestationResponseJson; // JSON string of AuthenticatorAttestationResponse

    private String clientExtensionsJson; // JSON string of ClientRegistrationExtensionOutputs, optional

    @NotBlank
    private String originalCreationOptionsJson; // JSON string of PublicKeyCredentialCreationOptions
}

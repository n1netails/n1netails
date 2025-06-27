package com.n1netails.n1netails.api.model.request.passkey;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class FinishAuthenticationRequest {
    @NotBlank
    private String assertionResponseJson; // JSON string of AuthenticatorAssertionResponse

    @NotBlank
    private String originalRequestOptionsJson; // JSON string of PublicKeyCredentialRequestOptions

    // Username might be derived from the assertion or context, not always needed here explicitly
    // if the originalRequestOptionsJson contains enough context or if session is used.
    // For discoverable credentials, username comes from the assertion result.
}

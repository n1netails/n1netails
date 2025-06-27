package com.n1netails.n1netails.api.model.request.passkey;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Email;

@Data
public class StartRegistrationRequest {
    @NotBlank
    @Email
    private String email;
    // The client origin and RP details will be determined server-side or from config
}

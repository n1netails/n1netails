package com.n1netails.n1netails.api.model.request.passkey;

import lombok.Data;

import jakarta.validation.constraints.Email;

@Data
public class StartAuthenticationRequest {
    @Email // Email can be null (for discoverable) but if provided, must be valid format
    private String email; // Optional, for discoverable credentials this might be null
}

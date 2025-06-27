package com.n1netails.n1netails.api.model.response.passkey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationSuccessResponse {
    private boolean success;
    private String username;
    private String message;
    // private String token; // Potentially include a JWT token here upon successful passkey login
}

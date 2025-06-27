package com.n1netails.n1netails.api.model.response.passkey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasskeyAuthenticationResponse {
    private String token;
    private String username;
    private String message;
}

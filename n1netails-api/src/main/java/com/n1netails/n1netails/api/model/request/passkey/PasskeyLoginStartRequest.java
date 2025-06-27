package com.n1netails.n1netails.api.model.request.passkey;

import lombok.Data;

@Data
public class PasskeyLoginStartRequest {
    // Username might be optional if relying on discoverable credentials (passkeys)
    private String username;
}

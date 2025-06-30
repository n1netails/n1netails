package com.n1netails.n1netails.api.model.dto.passkey;

import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasskeyAuthenticationStartResponseDto {
    private String flowId; // To correlate start and finish requests
    private PublicKeyCredentialRequestOptions options;
}

package com.n1netails.n1netails.api.model.dto.passkey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasskeyApiResponseDto {
    private boolean success;
    private String message;
}

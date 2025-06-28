package com.n1netails.n1netails.api.model.dto.passkey;

import com.n1netails.n1netails.api.model.entity.UsersEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasskeyAuthenticationResponseDto {
    private boolean success;
    private String message;
    private String jwtToken;
    private UsersEntity user;

    public PasskeyAuthenticationResponseDto(
            boolean success,
            String message,
            String jwtToken,
            UsersEntity user
    ) {
        this.success = success;
        this.message = message;
        this.jwtToken = jwtToken;
        this.user = user;
    }

     public PasskeyAuthenticationResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}

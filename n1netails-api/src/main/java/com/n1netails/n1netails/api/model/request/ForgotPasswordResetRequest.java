package com.n1netails.n1netails.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * ForgotPasswordResetRequest
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordResetRequest {
    private UUID requestId;
    private String newRawPassword;
}

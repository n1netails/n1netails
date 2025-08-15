package com.n1netails.n1netails.api.exception.type;

/**
 * ForgotPasswordRequestExpiredException
 */
public class ForgotPasswordRequestExpiredException extends RuntimeException {
    public ForgotPasswordRequestExpiredException(String message) {
        super(message);
    }
}

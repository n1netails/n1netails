package com.n1netails.n1netails.api.exception.type;

public class PreviousPasswordMatchException extends RuntimeException {
    public PreviousPasswordMatchException(String message) {
        super(message);
    }
}

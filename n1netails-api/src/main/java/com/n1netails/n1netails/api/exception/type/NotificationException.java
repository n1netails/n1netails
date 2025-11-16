package com.n1netails.n1netails.api.exception.type;

public class NotificationException extends RuntimeException {
    public NotificationException(String message) {
        super(message);
    }

    public NotificationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

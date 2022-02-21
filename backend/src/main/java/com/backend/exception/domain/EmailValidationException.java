package com.backend.exception.domain;

public class EmailValidationException extends Exception {
    public EmailValidationException(String message) {
        super(message);
    }
}

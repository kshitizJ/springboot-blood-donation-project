package com.backend.exception.domain;

/**
 * PasswordDidNotMatchException
 */
public class PasswordDidNotMatchException extends Exception {
    public PasswordDidNotMatchException(String message) {
        super(message);
    }
}

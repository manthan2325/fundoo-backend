package org.example.exception;

public class InValidCredentialsException extends RuntimeException {
    public InValidCredentialsException(String message) {
        super(message);
    }
}

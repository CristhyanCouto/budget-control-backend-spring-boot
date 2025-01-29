package com.budget.control.backend.exception;

public class NonAuthorizedException extends RuntimeException {
    public NonAuthorizedException(String message) {
        super(message);
    }
}

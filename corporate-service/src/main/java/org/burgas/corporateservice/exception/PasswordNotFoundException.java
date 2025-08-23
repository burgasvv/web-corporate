package org.burgas.corporateservice.exception;

public class PasswordNotFoundException extends RuntimeException {

    public PasswordNotFoundException(String message) {
        super(message);
    }
}

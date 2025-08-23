package org.burgas.corporateservice.exception;

public class PasswordMatchesException extends RuntimeException {

    public PasswordMatchesException(String message) {
        super(message);
    }
}

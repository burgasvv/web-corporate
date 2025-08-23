package org.burgas.corporateservice.exception;

public class IdentityNotAuthenticatedException extends RuntimeException {

    public IdentityNotAuthenticatedException(String message) {
        super(message);
    }
}

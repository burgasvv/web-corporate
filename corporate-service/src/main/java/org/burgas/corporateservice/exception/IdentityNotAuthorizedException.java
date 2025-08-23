package org.burgas.corporateservice.exception;

public class IdentityNotAuthorizedException extends RuntimeException {

    public IdentityNotAuthorizedException(String message) {
        super(message);
    }
}

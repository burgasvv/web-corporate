package org.burgas.corporateservice.exception;

public class WrongAuthorityForEmployeeException extends RuntimeException {

    public WrongAuthorityForEmployeeException(String message) {
        super(message);
    }
}

package org.burgas.corporateservice.exception;

public class OfficeNotFoundException extends RuntimeException {

    public OfficeNotFoundException(String message) {
        super(message);
    }
}

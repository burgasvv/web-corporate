package org.burgas.corporateservice.exception;

public class CorporationNotFoundException extends RuntimeException {

    public CorporationNotFoundException(String message) {
        super(message);
    }
}

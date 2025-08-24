package org.burgas.corporateservice.exception;

public class WrongEmployeeOrCorporationException extends RuntimeException {

    public WrongEmployeeOrCorporationException(String message) {
        super(message);
    }
}

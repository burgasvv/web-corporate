package org.burgas.corporateservice.message;

import lombok.Getter;

@Getter
public enum OfficeMessages {

    OFFICE_DELETED("Office deleted"),
    OFFICE_NOT_FOUND("Office not found"),
    OFFICE_FIELD_EMPTY("Office field is empty"),
    OFFICE_CORPORATION_FIELD_EMPTY("Office corporation field is empty"),
    OFFICE_ADDRESS_FIELD_EMPTY("Office address field is empty");

    private final String message;

    OfficeMessages(String message) {
        this.message = message;
    }
}

package org.burgas.corporateservice.message;

import lombok.Getter;

@Getter
public enum CorporationMessages {

    IDENTITY_NOT_DIRECTOR("Identity not director of this corporation"),
    CORPORATION_DIRECTOR_ADDED("Corporation director already added"),
    CORPORATION_WRONG_DIRECTOR("Wrong director id"),
    CORPORATION_DIRECTOR_ID_EMPTY("Corporation director id is empty"),
    CORPORATION_DELETED("Corporation deleted"),
    CORPORATION_NOT_FOUND("Corporation not found"),
    CORPORATION_FIELD_NAME_EMPTY("Corporation field name is empty"),
    CORPORATION_FIELD_DESCRIPTION_EMPTY("Corporation field description is empty");

    private final String message;

    CorporationMessages(String message) {
        this.message = message;
    }
}

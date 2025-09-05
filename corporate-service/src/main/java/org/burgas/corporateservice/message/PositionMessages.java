package org.burgas.corporateservice.message;

import lombok.Getter;

@Getter
public enum PositionMessages {

    POSITION_DELETED("Position deleted"),
    POSITION_NOT_FOUND("Position not found"),
    POSITION_FIELD_NAME_EMPTY("Position field name is empty"),
    POSITION_FIELD_DESCRIPTION("Position field description is empty"),
    POSITION_FIELD_DEPARTMENT("Position field department is empty");

    private final String message;

    PositionMessages(String message) {
        this.message = message;
    }
}

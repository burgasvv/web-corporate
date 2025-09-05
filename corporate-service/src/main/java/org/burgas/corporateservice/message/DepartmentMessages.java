package org.burgas.corporateservice.message;

import lombok.Getter;

@Getter
public enum DepartmentMessages {

    DEPARTMENT_DELETED("Department deleted"),
    DEPARTMENT_NOT_FOUND("Department not found"),
    DEPARTMENT_FIELD_NAME_EMPTY("Department field name is empty"),
    DEPARTMENT_FIELD_DESCRIPTION_EMPTY("Department field description is empty"),
    DEPARTMENT_FIELD_CORPORATION_EMPTY("Department field corporation is empty");

    private final String message;

    DepartmentMessages(String message) {
        this.message = message;
    }
}

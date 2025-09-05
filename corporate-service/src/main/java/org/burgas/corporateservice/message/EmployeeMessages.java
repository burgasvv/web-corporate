package org.burgas.corporateservice.message;

import lombok.Getter;

@Getter
public enum EmployeeMessages {

    EMPLOYEE_OFFICE_MATCHES("Employee office matches"),
    EMPLOYEE_TRANSFER("Employee was transfer"),
    IDENTITY_NOT_EMPLOYEE("Identity not employee"),
    IDENTITY_NOT_DIRECTOR("Identity not director"),
    EMPLOYEE_DELETED("Employee deleted"),
    EMPLOYEE_NOT_FOUND("Employee not found"),
    EMPLOYEE_IDENTITY_FIELD_EMPTY("Employee identity field is empty"),
    EMPLOYEE_POSITION_FIELD_EMPTY("Employee identity field is empty"),
    EMPLOYEE_FIRSTNAME_FIELD_EMPTY("Employee first name field is empty"),
    EMPLOYEE_LASTNAME_FIELD_EMPTY("Employee last name field is empty"),
    EMPLOYEE_PATRONYMIC_FIELD_EMPTY("Employee patronymic field is empty"),
    EMPLOYEE_ABOUT_FIELD_EMPTY("Employee about field is empty"),
    EMPLOYEE_ADDRESS_FIELD_EMPTY("Employee address field is empty"),
    EMPLOYEE_OFFICE_FIELD_EMPTY("Employee office field is empty"),
    EMPLOYEE_OFFICE_ID_EMPTY("Employee office id is empty"),
    EMPLOYEE_OFFICE_CORPORATION_FIELD_EMPTY("Employee office corporation field is empty"),
    EMPLOYEE_OFFICE_ADDRESS_FIELD_EMPTY("Employee office address field is empty");

    private final String message;

    EmployeeMessages(String message) {
        this.message = message;
    }
}

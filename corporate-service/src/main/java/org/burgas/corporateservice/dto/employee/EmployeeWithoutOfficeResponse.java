package org.burgas.corporateservice.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.burgas.corporateservice.dto.identity.IdentityWithoutEmployeeResponse;
import org.burgas.corporateservice.entity.Address;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class EmployeeWithoutOfficeResponse {

    private UUID id;
    private IdentityWithoutEmployeeResponse identity;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String about;
    private Address address;
}

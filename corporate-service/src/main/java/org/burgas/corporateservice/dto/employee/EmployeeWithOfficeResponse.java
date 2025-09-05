package org.burgas.corporateservice.dto.employee;

import lombok.*;
import org.burgas.corporateservice.dto.Response;
import org.burgas.corporateservice.dto.identity.IdentityWithoutEmployeeResponse;
import org.burgas.corporateservice.dto.office.OfficeWithoutEmployeesResponse;
import org.burgas.corporateservice.dto.position.PositionWithoutEmployeeResponse;
import org.burgas.corporateservice.entity.Address;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class EmployeeWithOfficeResponse extends Response {

    private UUID id;
    private IdentityWithoutEmployeeResponse identity;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String about;
    private Address address;
    private PositionWithoutEmployeeResponse position;
    private OfficeWithoutEmployeesResponse office;
}

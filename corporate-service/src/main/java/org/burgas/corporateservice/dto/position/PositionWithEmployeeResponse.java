package org.burgas.corporateservice.dto.position;

import lombok.*;
import org.burgas.corporateservice.dto.Response;
import org.burgas.corporateservice.dto.department.DepartmentWithoutOfficesResponse;
import org.burgas.corporateservice.dto.employee.EmployeeWithoutOfficeResponse;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class PositionWithEmployeeResponse extends Response {

    private UUID id;
    private String name;
    private String description;
    private DepartmentWithoutOfficesResponse department;
    private EmployeeWithoutOfficeResponse employee;
}

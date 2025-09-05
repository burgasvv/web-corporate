package org.burgas.corporateservice.dto.position;

import lombok.*;
import org.burgas.corporateservice.dto.Response;
import org.burgas.corporateservice.dto.department.DepartmentWithOfficesResponse;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class PositionWithoutEmployeeResponse extends Response {

    private UUID id;
    private String name;
    private String description;
    private DepartmentWithOfficesResponse departmentWithOfficesResponse;
}

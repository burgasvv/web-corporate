package org.burgas.corporateservice.dto.department;

import lombok.*;
import org.burgas.corporateservice.dto.Response;
import org.burgas.corporateservice.dto.office.OfficeWithoutEmployeesResponse;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class DepartmentWithOfficesResponse extends Response {

    private UUID id;
    private String name;
    private String description;
    private List<OfficeWithoutEmployeesResponse> offices;
}

package org.burgas.corporateservice.dto.office;

import lombok.*;
import org.burgas.corporateservice.dto.Response;
import org.burgas.corporateservice.dto.corporation.CorporationResponse;
import org.burgas.corporateservice.dto.employee.EmployeeWithoutOfficeResponse;
import org.burgas.corporateservice.entity.Address;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class OfficeWithEmployeesResponse extends Response {

    private CorporationResponse corporation;
    private Address address;
    private List<EmployeeWithoutOfficeResponse> employees;
}

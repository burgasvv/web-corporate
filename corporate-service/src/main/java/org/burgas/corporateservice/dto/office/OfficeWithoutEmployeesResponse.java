package org.burgas.corporateservice.dto.office;

import lombok.*;
import org.burgas.corporateservice.dto.Response;
import org.burgas.corporateservice.dto.corporation.CorporationWithOfficesResponse;
import org.burgas.corporateservice.dto.corporation.CorporationWithoutOfficesResponse;
import org.burgas.corporateservice.entity.Address;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class OfficeWithoutEmployeesResponse extends Response {

    private CorporationWithoutOfficesResponse corporation;
    private Address address;
}

package org.burgas.corporateservice.dto.office;

import lombok.*;
import org.burgas.corporateservice.dto.Response;
import org.burgas.corporateservice.dto.corporation.CorporationResponse;
import org.burgas.corporateservice.entity.Address;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class OfficeWithoutEmployeesResponse extends Response {

    private CorporationResponse corporation;
    private Address address;
}

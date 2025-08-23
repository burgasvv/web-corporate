package org.burgas.corporateservice.dto.office;

import lombok.*;
import org.burgas.corporateservice.dto.Request;
import org.burgas.corporateservice.entity.Address;
import org.burgas.corporateservice.entity.OfficePK;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class OfficeRequest extends Request {

    private OfficePK office;
    private Address newAddress;
    private List<UUID> employeeIds;
}

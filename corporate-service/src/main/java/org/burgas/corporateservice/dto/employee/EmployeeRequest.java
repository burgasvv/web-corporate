package org.burgas.corporateservice.dto.employee;

import lombok.*;
import org.burgas.corporateservice.dto.Request;
import org.burgas.corporateservice.entity.Address;
import org.burgas.corporateservice.entity.OfficePK;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class EmployeeRequest extends Request {

    private UUID id;
    private UUID identityId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String about;
    private Address address;
    private OfficePK office;
}

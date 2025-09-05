package org.burgas.corporateservice.dto.department;

import lombok.*;
import org.burgas.corporateservice.dto.Response;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class DepartmentWithoutOfficesResponse extends Response {

    private UUID id;
    private String name;
    private String description;
}

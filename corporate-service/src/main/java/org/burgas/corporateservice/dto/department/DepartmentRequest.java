package org.burgas.corporateservice.dto.department;

import lombok.*;
import org.burgas.corporateservice.dto.Request;
import org.burgas.corporateservice.entity.OfficePK;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class DepartmentRequest extends Request {

    private UUID id;
    private String name;
    private String description;
    private UUID corporationId;
    private List<OfficePK> officePKS;
}

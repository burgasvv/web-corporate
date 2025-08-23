package org.burgas.corporateservice.dto.corporation;

import lombok.*;
import org.burgas.corporateservice.dto.Request;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class CorporationRequest extends Request {

    private UUID id;
    private String name;
    private String description;
    private UUID directorId;
}

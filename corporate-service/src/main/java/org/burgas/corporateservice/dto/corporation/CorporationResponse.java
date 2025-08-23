package org.burgas.corporateservice.dto.corporation;

import lombok.*;
import org.burgas.corporateservice.dto.Response;
import org.burgas.corporateservice.entity.Media;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class CorporationResponse extends Response {

    private UUID id;
    private String name;
    private String description;
    private Long officesAmount;
    private Long employeesAmount;
    private List<UUID> directors;
    private Media image;
}

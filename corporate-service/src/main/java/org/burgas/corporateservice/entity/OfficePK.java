package org.burgas.corporateservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public final class OfficePK implements Serializable {

    @Column(name = "corporation_id")
    private UUID corporationId;

    @Column(name = "address_id")
    private UUID addressId;
}

package org.burgas.corporateservice.dto.identity;

import lombok.*;
import org.burgas.corporateservice.dto.Response;
import org.burgas.corporateservice.entity.Authority;
import org.burgas.corporateservice.entity.Media;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class IdentityWithoutEmployeeResponse extends Response {

    private UUID id;
    private Authority authority;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Boolean enabled;
    private Media image;
}

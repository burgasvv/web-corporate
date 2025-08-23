package org.burgas.corporateservice.dto.identity;

import lombok.*;
import org.burgas.corporateservice.dto.Request;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class IdentityRequest extends Request {

    private UUID id;
    private String username;
    private String password;
    private String email;
    private String phone;
}

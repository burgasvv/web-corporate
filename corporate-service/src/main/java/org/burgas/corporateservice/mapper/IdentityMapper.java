package org.burgas.corporateservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.identity.IdentityRequest;
import org.burgas.corporateservice.dto.identity.IdentityWithEmployeeResponse;
import org.burgas.corporateservice.entity.Authority;
import org.burgas.corporateservice.entity.Identity;
import org.burgas.corporateservice.mapper.contract.EntityMapper;
import org.burgas.corporateservice.repository.IdentityRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.burgas.corporateservice.message.IdentityMessages.*;

@Component
@RequiredArgsConstructor
public final class IdentityMapper implements EntityMapper<IdentityRequest, Identity, IdentityWithEmployeeResponse> {

    private final IdentityRepository identityRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeMapper employeeMapper;

    @Override
    public Identity toEntity(IdentityRequest identityRequest) {
        UUID identityId = this.handleData(identityRequest.getId(), UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)));
        return this.identityRepository.findById(identityId)
                .map(
                        identity -> Identity.builder()
                                .id(identity.getId())
                                .authority(identity.getAuthority())
                                .username(this.handleData(identityRequest.getUsername(), identity.getUsernameNotUserDetails()))
                                .password(identity.getPassword())
                                .email(this.handleData(identityRequest.getEmail(), identity.getEmail()))
                                .phone(this.handleData(identityRequest.getPhone(), identity.getPhone()))
                                .enabled(identity.getEnabled())
                                .employee(identity.getEmployee())
                                .build()
                )
                .orElseGet(
                        () -> {
                            String password = this.handleDataThrowable(identityRequest.getPassword(), IDENTITY_PASSWORD_FIELD_EMPTY.getMessage());
                            return Identity.builder()
                                    .authority(Authority.USER)
                                    .username(this.handleDataThrowable(identityRequest.getUsername(), IDENTITY_USERNAME_FIELD_EMPTY.getMessage()))
                                    .password(this.passwordEncoder.encode(password))
                                    .email(this.handleDataThrowable(identityRequest.getEmail(), IDENTITY_EMAIL_FIELD_EMPTY.getMessage()))
                                    .phone(this.handleDataThrowable(identityRequest.getPhone(), IDENTITY_PHONE_FILED_EMPTY.getMessage()))
                                    .enabled(true)
                                    .build();
                        }
                );
    }

    @Override
    public IdentityWithEmployeeResponse toResponse(Identity identity) {
        return IdentityWithEmployeeResponse.builder()
                .id(identity.getId())
                .authority(identity.getAuthority())
                .username(identity.getUsernameNotUserDetails())
                .password(identity.getPassword())
                .email(identity.getEmail())
                .phone(identity.getPhone())
                .enabled(identity.getEnabled())
                .employee(
                        Optional.ofNullable(identity.getEmployee())
                                .map(this.employeeMapper::toResponse)
                                .orElse(null)
                )
                .image(identity.getImage())
                .build();
    }
}

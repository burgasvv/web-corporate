package org.burgas.corporateservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.department.DepartmentWithoutOfficesResponse;
import org.burgas.corporateservice.dto.employee.EmployeeWithoutOfficeResponse;
import org.burgas.corporateservice.dto.identity.IdentityWithoutEmployeeResponse;
import org.burgas.corporateservice.dto.position.PositionRequest;
import org.burgas.corporateservice.dto.position.PositionWithEmployeeResponse;
import org.burgas.corporateservice.entity.Department;
import org.burgas.corporateservice.entity.Position;
import org.burgas.corporateservice.mapper.contract.EntityMapper;
import org.burgas.corporateservice.repository.DepartmentRepository;
import org.burgas.corporateservice.repository.PositionRepository;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.burgas.corporateservice.message.PositionMessages.*;

@Component
@RequiredArgsConstructor
public final class PositionMapper implements EntityMapper<PositionRequest, Position, PositionWithEmployeeResponse> {

    private final PositionRepository positionRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public Position toEntity(PositionRequest positionRequest) {
        UUID positionId = this.handleData(positionRequest.getId(), UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)));
        return this.positionRepository.findById(positionId)
                .map(
                        position -> {
                            String positionName = this.handleData(positionRequest.getName(), position.getName());
                            String descriptionName = this.handleData(positionRequest.getDescription(), position.getDescription());

                            UUID departmentId = this.handleData(
                                    positionRequest.getDepartmentId(), UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8))
                            );
                            Department department = this.handleData(
                                    this.departmentRepository.findById(departmentId).orElse(null), position.getDepartment()
                            );

                            return Position.builder()
                                    .id(position.getId())
                                    .name(positionName)
                                    .description(descriptionName)
                                    .department(department)
                                    .build();
                        }
                )
                .orElseGet(
                        () -> {
                            String positionName = this.handleDataThrowable(positionRequest.getName(), POSITION_FIELD_NAME_EMPTY.getMessage());
                            String positionDescription = this.handleDataThrowable(
                                    positionRequest.getDescription(), POSITION_FIELD_DESCRIPTION.getMessage()
                            );

                            UUID departmentId = positionRequest.getDepartmentId() == null ?
                                    UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) : positionRequest.getDepartmentId();
                            Department department = this.handleDataThrowable(
                                    this.departmentRepository.findById(departmentId).orElse(null), POSITION_FIELD_DEPARTMENT.getMessage()
                            );

                            return Position.builder()
                                    .name(positionName)
                                    .description(positionDescription)
                                    .department(department)
                                    .build();
                        }
                );
    }

    public PositionWithEmployeeResponse toResponse(Position position) {
        return PositionWithEmployeeResponse.builder()
                .id(position.getId())
                .name(position.getName())
                .description(position.getDescription())
                .department(
                        Optional.ofNullable(position.getDepartment())
                                .map(
                                        department -> DepartmentWithoutOfficesResponse.builder()
                                                .id(department.getId())
                                                .name(department.getName())
                                                .description(department.getDescription())
                                                .build()
                                )
                                .orElse(null)
                )
                .employee(
                        Optional.ofNullable(position.getEmployee())
                                .map(
                                        employee -> EmployeeWithoutOfficeResponse.builder()
                                                .id(employee.getId())
                                                .identity(
                                                        Optional.ofNullable(employee.getIdentity())
                                                                .map(
                                                                        identity -> IdentityWithoutEmployeeResponse.builder()
                                                                                .id(identity.getId())
                                                                                .authority(identity.getAuthority())
                                                                                .username(identity.getUsernameNotUserDetails())
                                                                                .password(identity.getPassword())
                                                                                .email(identity.getEmail())
                                                                                .phone(identity.getPhone())
                                                                                .enabled(identity.getEnabled())
                                                                                .image(identity.getImage())
                                                                                .build()
                                                                )
                                                                .orElse(null)
                                                )
                                                .firstName(employee.getFirstName())
                                                .lastName(employee.getLastName())
                                                .patronymic(employee.getPatronymic())
                                                .about(employee.getAbout())
                                                .address(employee.getAddress())
                                                .build()
                                )
                                .orElse(null)
                )
                .build();
    }
}

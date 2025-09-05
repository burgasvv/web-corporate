package org.burgas.corporateservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.department.DepartmentRequest;
import org.burgas.corporateservice.dto.department.DepartmentWithOfficesResponse;
import org.burgas.corporateservice.dto.office.OfficeWithoutEmployeesResponse;
import org.burgas.corporateservice.entity.Corporation;
import org.burgas.corporateservice.entity.Department;
import org.burgas.corporateservice.mapper.contract.EntityMapper;
import org.burgas.corporateservice.repository.AddressRepository;
import org.burgas.corporateservice.repository.CorporationRepository;
import org.burgas.corporateservice.repository.DepartmentRepository;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.burgas.corporateservice.message.DepartmentMessages.*;

@Component
@RequiredArgsConstructor
public final class DepartmentMapper implements EntityMapper<DepartmentRequest, Department, DepartmentWithOfficesResponse> {

    private final DepartmentRepository departmentRepository;
    private final AddressRepository addressRepository;
    private final CorporationRepository corporationRepository;
    private final CorporationMapper corporationMapper;

    @Override
    public Department toEntity(DepartmentRequest departmentRequest) {
        UUID departmentId = this.handleData(departmentRequest.getId(), UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)));
        return this.departmentRepository.findById(departmentId)
                .map(
                        department -> {
                            String departmentName = this.handleData(departmentRequest.getName(), department.getName());
                            String departmentDescription = this.handleData(departmentRequest.getDescription(), department.getDescription());
                            UUID corporationId = this.handleData(
                                    departmentRequest.getCorporationId(), UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8))
                            );
                            Corporation corporation = this.handleData(
                                    this.corporationRepository.findById(corporationId).orElse(null),
                                    department.getCorporation()
                            );

                            return Department.builder()
                                    .id(department.getId())
                                    .name(departmentName)
                                    .description(departmentDescription)
                                    .corporation(corporation)
                                    .build();
                        }

                )
                .orElseGet(
                        () -> {
                            String departmentName = this.handleDataThrowable(departmentRequest.getName(), DEPARTMENT_FIELD_NAME_EMPTY.getMessage());
                            String departmentDescription = this.handleDataThrowable(
                                    departmentRequest.getDescription(), DEPARTMENT_FIELD_DESCRIPTION_EMPTY.getMessage()
                            );
                            UUID corporationId = this.handleData(
                                    departmentRequest.getCorporationId(), UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8))
                            );
                            Corporation corporation = this.handleDataThrowable(
                                    this.corporationRepository.findById(corporationId).orElse(null),
                                    DEPARTMENT_FIELD_CORPORATION_EMPTY.getMessage()
                            );

                            return Department.builder()
                                    .name(departmentName)
                                    .description(departmentDescription)
                                    .corporation(corporation)
                                    .build();
                        }
                );
    }

    @Override
    public DepartmentWithOfficesResponse toResponse(Department department) {
        return DepartmentWithOfficesResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .offices(
                        department.getOffices() == null ? null : department.getOffices()
                                .stream()
                                .map(
                                        office -> OfficeWithoutEmployeesResponse.builder()
                                                .address(this.addressRepository.findById(office.getOfficePK().getAddressId()).orElse(null))
                                                .corporation(
                                                        this.corporationRepository.findById(office.getOfficePK().getCorporationId())
                                                                .map(this.corporationMapper::toCorporationWithoutOfficesResponse)
                                                                .orElse(null)
                                                )
                                                .build()
                                )
                                .toList()
                )
                .build();
    }
}

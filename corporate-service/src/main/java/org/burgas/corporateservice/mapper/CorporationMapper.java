package org.burgas.corporateservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.corporation.CorporationRequest;
import org.burgas.corporateservice.dto.corporation.CorporationWithOfficesResponse;
import org.burgas.corporateservice.dto.corporation.CorporationWithoutOfficesResponse;
import org.burgas.corporateservice.dto.department.DepartmentWithOfficesResponse;
import org.burgas.corporateservice.dto.department.DepartmentWithoutOfficesResponse;
import org.burgas.corporateservice.dto.office.OfficeWithoutEmployeesResponse;
import org.burgas.corporateservice.entity.Corporation;
import org.burgas.corporateservice.entity.Employee;
import org.burgas.corporateservice.entity.Office;
import org.burgas.corporateservice.exception.EmptyDirectorIdException;
import org.burgas.corporateservice.exception.WrongDirectorIdException;
import org.burgas.corporateservice.mapper.contract.EntityMapper;
import org.burgas.corporateservice.repository.AddressRepository;
import org.burgas.corporateservice.repository.CorporationRepository;
import org.burgas.corporateservice.repository.EmployeeRepository;
import org.burgas.corporateservice.repository.OfficeRepository;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.burgas.corporateservice.message.CorporationMessages.*;

@Component
@RequiredArgsConstructor
public final class CorporationMapper implements EntityMapper<CorporationRequest, Corporation, CorporationWithOfficesResponse> {

    private final CorporationRepository corporationRepository;
    private final OfficeRepository officeRepository;
    private final EmployeeRepository employeeRepository;
    private final AddressRepository addressRepository;

    @Override
    public Corporation toEntity(CorporationRequest corporationRequest) {
        if (corporationRequest.getDirectorId() == null)
            throw new EmptyDirectorIdException(CORPORATION_DIRECTOR_ID_EMPTY.getMessage());

        UUID corporationId = this.handleData(corporationRequest.getId(), UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)));
        List<Office> offices = this.officeRepository.findOfficesByCorporationId(corporationId);
        List<Employee> employees = this.employeeRepository.findEmployeesByCorporationId(corporationId);

        return this.corporationRepository.findById(corporationId)
                .map(
                        corporation -> {
                            if (!corporation.getDirectors().contains(corporationRequest.getDirectorId()))
                                throw new WrongDirectorIdException(CORPORATION_WRONG_DIRECTOR.getMessage());

                            String corporationName = this.handleData(corporationRequest.getName(), corporation.getName());
                            String corporationDescription = this.handleData(corporationRequest.getDescription(), corporation.getDescription());
                            long officesAmount = offices == null || offices.isEmpty() ? 0L : offices.size();
                            long employeesAmount = employees == null || employees.isEmpty() ? 0L : employees.size();

                            return Corporation.builder()
                                    .id(corporation.getId())
                                    .name(corporationName)
                                    .description(corporationDescription)
                                    .officesAmount(officesAmount)
                                    .employeesAmount(employeesAmount)
                                    .directors(corporation.getDirectors())
                                    .build();
                        }
                )
                .orElseGet(
                        () -> {
                            String corporationName = this.handleDataThrowable(
                                    corporationRequest.getName(), CORPORATION_FIELD_NAME_EMPTY.getMessage()
                            );
                            String corporationDescription = this.handleDataThrowable(
                                    corporationRequest.getDescription(), CORPORATION_FIELD_DESCRIPTION_EMPTY.getMessage()
                            );
                            long officeAmount = offices == null || offices.isEmpty() ? 0L : offices.size();
                            long employeesAmount = employees == null || employees.isEmpty() ? 0L : employees.size();
                            ArrayList<UUID> directors = new ArrayList<>(List.of(corporationRequest.getDirectorId()));

                            return Corporation.builder()
                                    .name(corporationName)
                                    .description(corporationDescription)
                                    .officesAmount(officeAmount)
                                    .employeesAmount(employeesAmount)
                                    .directors(directors)
                                    .build();
                        }
                );
    }

    @Override
    public CorporationWithOfficesResponse toResponse(Corporation corporation) {
        return CorporationWithOfficesResponse.builder()
                .id(corporation.getId())
                .name(corporation.getName())
                .description(corporation.getDescription())
                .officesAmount(corporation.getOfficesAmount())
                .employeesAmount(corporation.getEmployeesAmount())
                .directors(corporation.getDirectors())
                .image(corporation.getImage())
                .departments(
                        corporation.getDepartments() == null ? null :
                        corporation.getDepartments()
                                .stream()
                                .map(
                                        department -> DepartmentWithOfficesResponse.builder()
                                                .id(department.getId())
                                                .name(department.getName())
                                                .description(department.getDescription())
                                                .offices(
                                                        department.getOffices() == null ? null : department.getOffices()
                                                                .stream()
                                                                .map(
                                                                        office -> OfficeWithoutEmployeesResponse.builder()
                                                                                .address(this.addressRepository.findById(office.getOfficePK().getAddressId()).orElse(null))
                                                                                .build()
                                                                )
                                                                .toList()
                                                )
                                                .build()
                                )
                                .toList()
                )
                .build();
    }

    public CorporationWithoutOfficesResponse toCorporationWithoutOfficesResponse(Corporation corporation) {
        return CorporationWithoutOfficesResponse.builder()
                .id(corporation.getId())
                .name(corporation.getName())
                .description(corporation.getDescription())
                .officesAmount(corporation.getOfficesAmount())
                .employeesAmount(corporation.getEmployeesAmount())
                .directors(corporation.getDirectors())
                .image(corporation.getImage())
                .departments(
                        corporation.getDepartments() == null ? null :
                                corporation.getDepartments()
                                        .stream()
                                        .map(
                                                department -> DepartmentWithoutOfficesResponse.builder()
                                                        .id(department.getId())
                                                        .name(department.getName())
                                                        .description(department.getDescription())
                                                        .build()
                                        )
                                        .toList()
                )
                .build();
    }
}

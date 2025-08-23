package org.burgas.corporateservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.corporation.CorporationRequest;
import org.burgas.corporateservice.dto.corporation.CorporationResponse;
import org.burgas.corporateservice.entity.Corporation;
import org.burgas.corporateservice.entity.Employee;
import org.burgas.corporateservice.entity.Office;
import org.burgas.corporateservice.exception.EmptyDirectorIdException;
import org.burgas.corporateservice.exception.WrongDirectorIdException;
import org.burgas.corporateservice.mapper.contract.EntityMapper;
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
public final class CorporationMapper implements EntityMapper<CorporationRequest, Corporation, CorporationResponse> {

    private final CorporationRepository corporationRepository;
    private final OfficeRepository officeRepository;
    private final EmployeeRepository employeeRepository;

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

                            return Corporation.builder()
                                    .id(corporation.getId())
                                    .name(this.handleData(corporationRequest.getName(), corporation.getName()))
                                    .description(this.handleData(corporationRequest.getDescription(), corporation.getDescription()))
                                    .officesAmount(offices == null || offices.isEmpty() ? 0L : offices.size())
                                    .employeesAmount(employees == null || employees.isEmpty() ? 0L : employees.size())
                                    .directors(corporation.getDirectors())
                                    .build();
                        }
                )
                .orElseGet(
                        () -> Corporation.builder()
                                .name(this.handleDataThrowable(corporationRequest.getName(), CORPORATION_FIELD_NAME_EMPTY.getMessage()))
                                .description(this.handleDataThrowable(corporationRequest.getDescription(), CORPORATION_FIELD_DESCRIPTION_EMPTY.getMessage()))
                                .officesAmount(offices == null || offices.isEmpty() ? 0L : offices.size())
                                .employeesAmount(employees == null || employees.isEmpty() ? 0L : employees.size())
                                .directors(new ArrayList<>(List.of(corporationRequest.getDirectorId())))
                                .build()
                );
    }

    @Override
    public CorporationResponse toResponse(Corporation corporation) {
        return CorporationResponse.builder()
                .id(corporation.getId())
                .name(corporation.getName())
                .description(corporation.getDescription())
                .officesAmount(corporation.getOfficesAmount())
                .employeesAmount(corporation.getEmployeesAmount())
                .directors(corporation.getDirectors())
                .image(corporation.getImage())
                .build();
    }
}

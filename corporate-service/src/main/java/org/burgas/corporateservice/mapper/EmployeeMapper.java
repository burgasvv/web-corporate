package org.burgas.corporateservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.employee.EmployeeRequest;
import org.burgas.corporateservice.dto.employee.EmployeeWithOfficeResponse;
import org.burgas.corporateservice.dto.identity.IdentityWithoutEmployeeResponse;
import org.burgas.corporateservice.dto.office.OfficeWithoutEmployeesResponse;
import org.burgas.corporateservice.entity.*;
import org.burgas.corporateservice.exception.CorporationNotFoundException;
import org.burgas.corporateservice.exception.EntityFieldEmptyException;
import org.burgas.corporateservice.exception.OfficeNotFoundException;
import org.burgas.corporateservice.mapper.contract.EntityMapper;
import org.burgas.corporateservice.repository.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.burgas.corporateservice.message.CorporationMessages.CORPORATION_NOT_FOUND;
import static org.burgas.corporateservice.message.EmployeeMessages.*;
import static org.burgas.corporateservice.message.OfficeMessages.OFFICE_NOT_FOUND;

@Component
@RequiredArgsConstructor
public final class EmployeeMapper implements EntityMapper<EmployeeRequest, Employee, EmployeeWithOfficeResponse> {

    private final EmployeeRepository employeeRepository;
    private final OfficeRepository officeRepository;
    private final AddressRepository addressRepository;

    private final CorporationRepository corporationRepository;
    private final CorporationMapper corporationMapper;
    private final IdentityRepository identityRepository;

    @Override
    public Employee toEntity(EmployeeRequest employeeRequest) {
        UUID employeeId = this.handleData(employeeRequest.getId(), UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)));
        return this.employeeRepository.findById(employeeId)
                .map(
                        employee -> {
                            Address newAddress = employeeRequest.getAddress();
                            OfficePK officePK = getOfficePK(employeeRequest);

                            return Employee.builder()
                                    .id(employee.getId())
                                    .identity(
                                            this.handleData(
                                                    this.identityRepository.findById(
                                                            employeeRequest.getIdentityId() == null ?
                                                                    UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) :
                                                                    employeeRequest.getIdentityId()
                                                            )
                                                            .orElse(null),
                                                    employee.getIdentity()
                                            )
                                    )
                                    .firstName(this.handleData(employeeRequest.getFirstName(), employee.getFirstName()))
                                    .lastName(this.handleData(employeeRequest.getLastName(), employee.getLastName()))
                                    .patronymic(this.handleData(employeeRequest.getPatronymic(), employee.getPatronymic()))
                                    .about(this.handleData(employeeRequest.getAbout(), employee.getAbout()))
                                    .address(newAddress == null ? employee.getAddress() : this.addressRepository.save(newAddress))
                                    .office(
                                            this.handleData(
                                                    this.officeRepository.findById(officePK).orElse(null),
                                                    employee.getOffice()
                                            )
                                    )
                                    .build();
                        }
                )
                .orElseGet(
                        () -> {
                            Address address = employeeRequest.getAddress();
                            if (address != null) {
                                address = this.addressRepository.save(address);

                            } else {
                                throw new EntityFieldEmptyException(EMPLOYEE_ADDRESS_FIELD_EMPTY.getMessage());
                            }
                            OfficePK officePK = getOfficePK(employeeRequest);

                            Corporation corporation = this.corporationRepository.findById(officePK.getCorporationId())
                                    .orElseThrow(() -> new CorporationNotFoundException(CORPORATION_NOT_FOUND.getMessage()));
                            corporation.setEmployeesAmount(corporation.getEmployeesAmount() + 1);
                            this.corporationRepository.save(corporation);

                            Office office = this.officeRepository.findById(officePK)
                                    .orElseThrow(() -> new OfficeNotFoundException(OFFICE_NOT_FOUND.getMessage()));
                            office.setEmployeesAmount(office.getEmployeesAmount() + 1);
                            office = this.officeRepository.save(office);

                            return Employee.builder()
                                    .identity(
                                            this.handleDataThrowable(
                                                    this.identityRepository.findById(
                                                                    employeeRequest.getIdentityId() == null ?
                                                                            UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) :
                                                                            employeeRequest.getIdentityId()
                                                            )
                                                            .orElse(null),
                                                    EMPLOYEE_IDENTITY_FIELD_EMPTY.getMessage()
                                            )
                                    )
                                    .firstName(this.handleDataThrowable(employeeRequest.getFirstName(), EMPLOYEE_FIRSTNAME_FIELD_EMPTY.getMessage()))
                                    .lastName(this.handleDataThrowable(employeeRequest.getLastName(), EMPLOYEE_LASTNAME_FIELD_EMPTY.getMessage()))
                                    .patronymic(this.handleDataThrowable(employeeRequest.getPatronymic(), EMPLOYEE_PATRONYMIC_FIELD_EMPTY.getMessage()))
                                    .about(this.handleDataThrowable(employeeRequest.getAbout(), EMPLOYEE_ABOUT_FIELD_EMPTY.getMessage()))
                                    .address(this.handleDataThrowable(address, EMPLOYEE_ADDRESS_FIELD_EMPTY.getMessage()))
                                    .office(office)
                                    .build();
                        }
                );
    }

    private static @NotNull OfficePK getOfficePK(EmployeeRequest employeeRequest) {
        OfficePK officePK = employeeRequest.getOffice();

        if (officePK != null) {
            if (officePK.getAddressId() == null)
                throw new EntityFieldEmptyException(EMPLOYEE_OFFICE_ADDRESS_FIELD_EMPTY.getMessage());

            if (officePK.getCorporationId() == null)
                throw new EntityFieldEmptyException(EMPLOYEE_OFFICE_CORPORATION_FIELD_EMPTY.getMessage());

        } else {
            throw new EntityFieldEmptyException(EMPLOYEE_OFFICE_ID_EMPTY.getMessage());
        }
        return officePK;
    }

    @Override
    public EmployeeWithOfficeResponse toResponse(Employee employee) {
        return EmployeeWithOfficeResponse.builder()
                .id(employee.getId())
                .identity(
                        Optional.ofNullable(employee.getIdentity())
                                .map(
                                        identity -> IdentityWithoutEmployeeResponse.builder()
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
                .office(
                        Optional.ofNullable(employee.getOffice())
                                .map(
                                        office -> OfficeWithoutEmployeesResponse.builder()
                                                .address(this.addressRepository.findById(office.getOfficePK().getAddressId()).orElse(null))
                                                .corporation(
                                                        this.corporationRepository.findById(office.getOfficePK().getCorporationId())
                                                                .map(this.corporationMapper::toResponse)
                                                                .orElse(null)
                                                )
                                                .build()
                                )
                                .orElse(null)
                )
                .build();
    }
}

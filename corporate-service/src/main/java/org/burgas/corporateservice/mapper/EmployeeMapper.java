package org.burgas.corporateservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.employee.EmployeeNoIdentityResponse;
import org.burgas.corporateservice.dto.employee.EmployeeRequest;
import org.burgas.corporateservice.dto.employee.EmployeeWithOfficeResponse;
import org.burgas.corporateservice.dto.identity.IdentityWithoutEmployeeResponse;
import org.burgas.corporateservice.dto.office.OfficeWithoutEmployeesResponse;
import org.burgas.corporateservice.dto.position.PositionWithoutEmployeeResponse;
import org.burgas.corporateservice.entity.*;
import org.burgas.corporateservice.exception.CorporationNotFoundException;
import org.burgas.corporateservice.exception.EntityFieldEmptyException;
import org.burgas.corporateservice.exception.OfficeNotFoundException;
import org.burgas.corporateservice.mapper.contract.EntityMapper;
import org.burgas.corporateservice.repository.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    private final PositionRepository positionRepository;

    @Override
    public Employee toEntity(EmployeeRequest employeeRequest) {
        UUID employeeId = this.handleData(employeeRequest.getId(), UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)));
        return this.employeeRepository.findById(employeeId)
                .map(
                        employee -> {
                            Address newAddress = employeeRequest.getAddress();
                            Address address = newAddress == null ? employee.getAddress() : this.addressRepository.save(newAddress);
                            OfficePK officePK = getOfficePK(employeeRequest);

                            UUID identityId = this.handleData(
                                    employeeRequest.getIdentityId(), UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8))
                            );
                            Identity identity = this.handleData(
                                    this.identityRepository.findById(identityId).orElse(null),
                                    employee.getIdentity()
                            );

                            UUID positionId = this.handleData(
                                    employeeRequest.getPositionId(), UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8))
                            );
                            Position position = this.handleData(
                                    this.positionRepository.findById(positionId).orElse(null),
                                    employee.getPosition()
                            );
                            String firstName = this.handleData(employeeRequest.getFirstName(), employee.getFirstName());
                            String lastName = this.handleData(employeeRequest.getLastName(), employee.getLastName());
                            String patronymic = this.handleData(employeeRequest.getPatronymic(), employee.getPatronymic());
                            String about = this.handleData(employeeRequest.getAbout(), employee.getAbout());
                            Office office = this.handleData(
                                    this.officeRepository.findById(officePK).orElse(null),
                                    employee.getOffice()
                            );

                            return Employee.builder()
                                    .id(employee.getId())
                                    .identity(identity)
                                    .position(position)
                                    .firstName(firstName)
                                    .lastName(lastName)
                                    .patronymic(patronymic)
                                    .about(about)
                                    .address(address)
                                    .office(office)
                                    .build();
                        }
                )
                .orElseGet(
                        () -> {
                            Address address = employeeRequest.getAddress();
                            if (address != null) {
                                address = this.addressRepository.save(address);
                                address = this.handleDataThrowable(address, EMPLOYEE_ADDRESS_FIELD_EMPTY.getMessage());

                            } else {
                                throw new EntityFieldEmptyException(EMPLOYEE_ADDRESS_FIELD_EMPTY.getMessage());
                            }
                            OfficePK officePK = getOfficePK(employeeRequest);

                            Corporation corporation = this.corporationRepository.findById(officePK.getCorporationId())
                                    .orElseThrow(() -> new CorporationNotFoundException(CORPORATION_NOT_FOUND.getMessage()));
                            corporation.setEmployeesAmount(corporation.getEmployeesAmount() + 1);

                            Office office = this.officeRepository.findById(officePK)
                                    .orElseThrow(() -> new OfficeNotFoundException(OFFICE_NOT_FOUND.getMessage()));
                            office.setEmployeesAmount(office.getEmployeesAmount() + 1);

                            UUID identityId = this.handleData(
                                    employeeRequest.getIdentityId(), UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8))
                            );
                            Identity identity = this.handleDataThrowable(
                                    this.identityRepository.findById(identityId).orElse(null),
                                    EMPLOYEE_IDENTITY_FIELD_EMPTY.getMessage()
                            );

                            UUID positionId = this.handleData(
                                    employeeRequest.getPositionId(), UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8))
                            );
                            Position position = this.handleDataThrowable(
                                    this.positionRepository.findById(positionId).orElse(null),
                                    EMPLOYEE_POSITION_FIELD_EMPTY.getMessage()
                            );
                            String firstName = this.handleDataThrowable(employeeRequest.getFirstName(), EMPLOYEE_FIRSTNAME_FIELD_EMPTY.getMessage());
                            String lastName = this.handleDataThrowable(employeeRequest.getLastName(), EMPLOYEE_LASTNAME_FIELD_EMPTY.getMessage());
                            String patronymic = this.handleDataThrowable(employeeRequest.getPatronymic(), EMPLOYEE_PATRONYMIC_FIELD_EMPTY.getMessage());
                            String about = this.handleDataThrowable(employeeRequest.getAbout(), EMPLOYEE_ABOUT_FIELD_EMPTY.getMessage());

                            return Employee.builder()
                                    .identity(identity)
                                    .position(position)
                                    .firstName(firstName)
                                    .lastName(lastName)
                                    .patronymic(patronymic)
                                    .about(about)
                                    .address(address)
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
                .position(
                        Optional.ofNullable(employee.getPosition())
                                .map(
                                        position -> PositionWithoutEmployeeResponse.builder()
                                                .id(position.getId())
                                                .name(position.getName())
                                                .description(position.getDescription())
                                                .build()
                                )
                                .orElse(null)
                )
                .office(getOffice(employee))
                .build();
    }

    public EmployeeNoIdentityResponse toEmployeeNoIdentityResponse(Employee employee) {
        return EmployeeNoIdentityResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .patronymic(employee.getPatronymic())
                .about(employee.getAbout())
                .address(employee.getAddress())
                .position(
                        Optional.ofNullable(employee.getPosition())
                                .map(
                                        position -> PositionWithoutEmployeeResponse.builder()
                                                .id(position.getId())
                                                .name(position.getName())
                                                .description(position.getDescription())
                                                .build()
                                )
                                .orElse(null)
                )
                .office(getOffice(employee))
                .build();
    }

    private @Nullable OfficeWithoutEmployeesResponse getOffice(Employee employee) {
        return Optional.ofNullable(employee.getOffice())
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
                .orElse(null);
    }
}

package org.burgas.corporateservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.employee.EmployeeWithoutOfficeResponse;
import org.burgas.corporateservice.dto.identity.IdentityWithoutEmployeeResponse;
import org.burgas.corporateservice.dto.office.OfficeRequest;
import org.burgas.corporateservice.dto.office.OfficeWithEmployeesResponse;
import org.burgas.corporateservice.entity.*;
import org.burgas.corporateservice.exception.CorporationNotFoundException;
import org.burgas.corporateservice.exception.EntityFieldEmptyException;
import org.burgas.corporateservice.mapper.contract.EntityMapper;
import org.burgas.corporateservice.repository.AddressRepository;
import org.burgas.corporateservice.repository.CorporationRepository;
import org.burgas.corporateservice.repository.EmployeeRepository;
import org.burgas.corporateservice.repository.OfficeRepository;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.burgas.corporateservice.message.CorporationMessages.CORPORATION_NOT_FOUND;
import static org.burgas.corporateservice.message.OfficeMessages.*;

@Component
@RequiredArgsConstructor
public final class OfficeMapper implements EntityMapper<OfficeRequest, Office, OfficeWithEmployeesResponse> {

    private final OfficeRepository officeRepository;

    private final CorporationRepository corporationRepository;
    private final CorporationMapper corporationMapper;

    private final AddressRepository addressRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public Office toEntity(OfficeRequest officeRequest) {
        UUID uuid = UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8));
        OfficePK officePK = officeRequest.getOffice() == null ||
                            officeRequest.getOffice().getCorporationId() == null ||
                            officeRequest.getOffice().getAddressId() == null
                ? OfficePK.builder().addressId(uuid).corporationId(uuid).build() : officeRequest.getOffice();

        return this.officeRepository.findById(officePK)
                .map(
                        office -> {
                            Address newAddress = officeRequest.getNewAddress();
                            if (newAddress != null) {
                                newAddress.setId(officeRequest.getOffice().getAddressId());
                                newAddress = this.addressRepository.save(newAddress);
                            }

                            List<Employee> employeesByIds =
                                    this.employeeRepository.findAllById(officeRequest.getEmployeeIds() == null ?
                                                    new ArrayList<>() : officeRequest.getEmployeeIds());
                            office.getEmployees().addAll(employeesByIds);

                            return Office.builder()
                                    .officePK(
                                            newAddress == null ? office.getOfficePK() : OfficePK.builder()
                                                    .addressId(newAddress.getId()).corporationId(office.getOfficePK().getCorporationId())
                                                    .build()
                                    )
                                    .employees(office.getEmployees())
                                    .employeesAmount((long) office.getEmployees().size())
                                    .build();
                        }
                )
                .orElseGet(
                        () -> {
                            if (officeRequest.getOffice() == null)
                                throw new EntityFieldEmptyException(OFFICE_FIELD_EMPTY.getMessage());

                            UUID corporationId = officeRequest.getOffice().getCorporationId();
                            if (corporationId == null)
                                throw new EntityFieldEmptyException(OFFICE_CORPORATION_FIELD_EMPTY.getMessage());

                            Address newAddress = officeRequest.getNewAddress();
                            UUID addressId = officeRequest.getOffice().getAddressId();
                            OfficePK build = OfficePK.builder().build();

                            if (newAddress != null && addressId == null) {
                                newAddress = this.addressRepository.save(newAddress);
                                build.setCorporationId(corporationId);
                                build.setAddressId(newAddress.getId());

                            } else {

                                if (addressId == null) {
                                    throw new EntityFieldEmptyException(OFFICE_ADDRESS_FIELD_EMPTY.getMessage());

                                } else {
                                    build.setCorporationId(corporationId);
                                    build.setAddressId(addressId);
                                }
                            }

                            List<Employee> employeesByIds =
                                    this.employeeRepository.findAllById(officeRequest.getEmployeeIds() == null ?
                                            new ArrayList<>() : officeRequest.getEmployeeIds());

                            Corporation corporation = this.corporationRepository.findById(build.getCorporationId())
                                    .orElseThrow(() -> new CorporationNotFoundException(CORPORATION_NOT_FOUND.getMessage()));
                            corporation.setOfficesAmount(corporation.getOfficesAmount() + 1);
                            this.corporationRepository.save(corporation);

                            return Office.builder()
                                    .officePK(build)
                                    .employees(employeesByIds)
                                    .employeesAmount((long) employeesByIds.size())
                                    .build();
                        }
                );
    }

    @Override
    public OfficeWithEmployeesResponse toResponse(Office office) {
        return OfficeWithEmployeesResponse.builder()
                .corporation(
                        this.corporationRepository.findById(office.getOfficePK().getCorporationId())
                                .map(this.corporationMapper::toResponse)
                                .orElse(null)
                )
                .address(this.addressRepository.findById(office.getOfficePK().getAddressId()).orElse(null))
                .employees(
                        this.employeeRepository.findEmployeesByOffice(office)
                                .stream()
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
                                .toList()
                )
                .build();
    }
}

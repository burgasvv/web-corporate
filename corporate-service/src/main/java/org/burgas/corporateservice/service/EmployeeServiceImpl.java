package org.burgas.corporateservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.employee.EmployeeRequest;
import org.burgas.corporateservice.dto.employee.EmployeeWithOfficeResponse;
import org.burgas.corporateservice.entity.*;
import org.burgas.corporateservice.exception.EmployeeNotFoundException;
import org.burgas.corporateservice.exception.EmployeeOfficeMatchesException;
import org.burgas.corporateservice.mapper.EmployeeMapper;
import org.burgas.corporateservice.message.EmployeeMessages;
import org.burgas.corporateservice.repository.EmployeeRepository;
import org.burgas.corporateservice.repository.OfficeRepository;
import org.burgas.corporateservice.service.contract.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.burgas.corporateservice.message.EmployeeMessages.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class EmployeeServiceImpl implements EmployeeService<EmployeeRequest, EmployeeWithOfficeResponse> {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final OfficeServiceImpl officeService;
    private final OfficeRepository officeRepository;

    public Employee findEmployee(final UUID employeeId) {
        return this.employeeRepository.findById(employeeId == null ? UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) : employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND.getMessage()));
    }

    @Override
    public List<EmployeeWithOfficeResponse> findByCorporationId(UUID corporationId) {
        return this.employeeRepository.findEmployeesByCorporationId(
                        corporationId == null ? UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) : corporationId
                )
                .stream()
                .map(this.employeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeWithOfficeResponse> findByOffice(OfficePK officePK) {
        Office office = this.officeService.findOffice(officePK);
        return this.employeeRepository.findEmployeesByOffice(office)
                .stream()
                .map(this.employeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeWithOfficeResponse findById(UUID employeeId) {
        return this.employeeMapper.toResponse(this.findEmployee(employeeId));
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public EmployeeWithOfficeResponse createOrUpdate(EmployeeRequest employeeRequest) {
        return this.employeeMapper.toResponse(
                this.employeeRepository.save(this.employeeMapper.toEntity(employeeRequest))
        );
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public String delete(UUID employeeId) {
        Employee employee = this.findEmployee(employeeId);

        Office office = employee.getOffice();
        office.setEmployeesAmount(office.getEmployeesAmount() - 1);

        Department department = employee.getPosition().getDepartment();
        Corporation corporation = department.getCorporation();
        corporation.setEmployeesAmount(corporation.getEmployeesAmount() - 1);

        this.employeeRepository.deleteEmployeeById(employee.getId());
        return EMPLOYEE_DELETED.getMessage();
    }

    @Transactional(
            isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public String transferToAnotherOffice(final UUID employeeId, final OfficePK officePK) {
        Employee employee = this.findEmployee(employeeId);
        Office oldOffice = employee.getOffice();
        Office newOffice = this.officeService.findOffice(officePK);

        if (oldOffice.equals(newOffice))
            throw new EmployeeOfficeMatchesException(EmployeeMessages.EMPLOYEE_OFFICE_MATCHES.getMessage());

        oldOffice.setEmployeesAmount(oldOffice.getEmployeesAmount() - 1);
        this.officeRepository.save(oldOffice);

        employee.setOffice(newOffice);
        this.employeeRepository.save(employee);

        newOffice.setEmployeesAmount(newOffice.getEmployeesAmount() + 1);
        this.officeRepository.save(newOffice);

        return EMPLOYEE_TRANSFER.getMessage();
    }
}

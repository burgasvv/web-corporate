package org.burgas.corporateservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.employee.EmployeeRequest;
import org.burgas.corporateservice.dto.employee.EmployeeWithOfficeResponse;
import org.burgas.corporateservice.entity.Employee;
import org.burgas.corporateservice.entity.Office;
import org.burgas.corporateservice.entity.OfficePK;
import org.burgas.corporateservice.exception.EmployeeNotFoundException;
import org.burgas.corporateservice.mapper.EmployeeMapper;
import org.burgas.corporateservice.message.EmployeeMessages;
import org.burgas.corporateservice.repository.EmployeeRepository;
import org.burgas.corporateservice.service.contract.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class EmployeeServiceImpl implements EmployeeService<EmployeeRequest, EmployeeWithOfficeResponse> {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final OfficeServiceImpl officeService;

    public Employee findEmployee(final UUID employeeId) {
        return this.employeeRepository.findById(employeeId == null ? UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) : employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(EmployeeMessages.EMPLOYEE_NOT_FOUND.getMessage()));
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
        this.employeeRepository.delete(employee);
        return EmployeeMessages.EMPLOYEE_DELETED.getMessage();
    }
}

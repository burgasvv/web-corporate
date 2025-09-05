package org.burgas.corporateservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.department.DepartmentRequest;
import org.burgas.corporateservice.dto.department.DepartmentWithOfficesResponse;
import org.burgas.corporateservice.entity.Corporation;
import org.burgas.corporateservice.entity.Department;
import org.burgas.corporateservice.entity.Office;
import org.burgas.corporateservice.exception.DepartmentNotFoundException;
import org.burgas.corporateservice.mapper.DepartmentMapper;
import org.burgas.corporateservice.repository.DepartmentRepository;
import org.burgas.corporateservice.repository.OfficeRepository;
import org.burgas.corporateservice.service.contract.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.burgas.corporateservice.message.DepartmentMessages.DEPARTMENT_DELETED;
import static org.burgas.corporateservice.message.DepartmentMessages.DEPARTMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class DepartmentServiceImpl implements DepartmentService<DepartmentRequest, DepartmentWithOfficesResponse> {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final CorporationService corporationService;
    private final OfficeRepository officeRepository;

    public Department findDepartment(final UUID departmentId) {
        return this.departmentRepository.findById(
                        departmentId == null ? UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) : departmentId
                )
                .orElseThrow(
                        () -> new DepartmentNotFoundException(DEPARTMENT_NOT_FOUND.getMessage())
                );
    }

    @Override
    public List<DepartmentWithOfficesResponse> findByCorporation(UUID corporationId) {
        Corporation corporation = this.corporationService.findCorporation(corporationId);
        return this.departmentRepository.findDepartmentsByCorporationId(corporation.getId())
                .stream()
                .map(this.departmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentWithOfficesResponse findById(UUID departmentId) {
        return this.departmentMapper.toResponse(this.findDepartment(departmentId));
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public DepartmentWithOfficesResponse createOrUpdate(DepartmentRequest departmentRequest) {
        Department department = this.departmentRepository.save(this.departmentMapper.toEntity(departmentRequest));
        if (departmentRequest.getOfficePKS() != null && !departmentRequest.getOfficePKS().isEmpty()) {

            List<Office> offices = departmentRequest.getOfficePKS()
                    .stream()
                    .map(officePK -> this.officeRepository.findById(officePK).orElse(null))
                    .collect(Collectors.toList());

            if (department.getOffices() != null) {
                department.addOffices(offices);

            } else {
                department.setOffices(offices);
                Department finalDepartment = department;
                department.getOffices().forEach(office -> office.getDepartments().add(finalDepartment));
            }
        }
        department = this.departmentRepository.save(department);
        return this.departmentMapper.toResponse(department);
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public String delete(UUID departmentId) {
        Department department = this.findDepartment(departmentId);
        this.departmentRepository.deleteDepartmentById(department.getId());
        return DEPARTMENT_DELETED.getMessage();
    }
}

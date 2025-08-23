package org.burgas.corporateservice.service.contract;

import org.burgas.corporateservice.dto.employee.EmployeeRequest;
import org.burgas.corporateservice.dto.employee.EmployeeWithOfficeResponse;
import org.burgas.corporateservice.entity.OfficePK;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface EmployeeService<T extends EmployeeRequest, S extends EmployeeWithOfficeResponse> {

    List<S> findByCorporationId(final UUID corporationId);

    List<S> findByOffice(final OfficePK officePK);

    S findById(final UUID employeeId);

    S createOrUpdate(T t);

    String delete(final UUID employeeId);
}

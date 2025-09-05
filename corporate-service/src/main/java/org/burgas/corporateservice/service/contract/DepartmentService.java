package org.burgas.corporateservice.service.contract;

import org.burgas.corporateservice.dto.department.DepartmentRequest;
import org.burgas.corporateservice.dto.department.DepartmentWithOfficesResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface DepartmentService<T extends DepartmentRequest, S extends DepartmentWithOfficesResponse> {

    List<S> findByCorporation(final UUID corporationId);

    S findById(final UUID departmentId);

    S createOrUpdate(final T t);

    String delete(final UUID departmentId);
}

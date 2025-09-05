package org.burgas.corporateservice.service.contract;

import org.burgas.corporateservice.dto.position.PositionRequest;
import org.burgas.corporateservice.dto.position.PositionWithEmployeeResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface PositionService<T extends PositionRequest, S extends PositionWithEmployeeResponse> {

    List<S> findByDepartmentId(final UUID departmentId);

    List<S> findByCorporationId(final UUID corporationId);

    S findById(final UUID positionId);

    S createOrUpdate(final T t);

    String delete(final UUID positionId);
}

package org.burgas.corporateservice.service.contract;

import org.burgas.corporateservice.dto.office.OfficeRequest;
import org.burgas.corporateservice.dto.office.OfficeWithEmployeesResponse;
import org.burgas.corporateservice.entity.OfficePK;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface OfficeService<T extends OfficeRequest, S extends OfficeWithEmployeesResponse> {

    List<S> findByCorporationId(final UUID corporationId);

    S findById(final OfficePK officePK);

    S createOrUpdate(final T t);

    String delete(final OfficePK officePK);
}

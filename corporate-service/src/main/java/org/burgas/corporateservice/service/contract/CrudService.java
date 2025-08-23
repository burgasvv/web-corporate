package org.burgas.corporateservice.service.contract;

import org.burgas.corporateservice.dto.Request;
import org.burgas.corporateservice.dto.Response;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface CrudService<T extends Request, V extends Response> {

    List<V> findAll();

    V findById(final UUID uuid);

    V createOrUpdate(T t);

    String delete(final UUID uuid);
}

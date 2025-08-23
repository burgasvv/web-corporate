package org.burgas.corporateservice.mapper.contract;

import org.burgas.corporateservice.dto.Request;
import org.burgas.corporateservice.dto.Response;
import org.burgas.corporateservice.entity.AbstractEntity;
import org.burgas.corporateservice.exception.EntityFieldEmptyException;
import org.springframework.stereotype.Component;

@Component
public interface EntityMapper<T extends Request, S extends AbstractEntity, V extends Response> {

    default <D> D handleData(final D requestData, final D entityData) {
        return requestData == null || requestData == "" ? entityData : requestData;
    }

    default <D> D handleDataThrowable(final D requestData, final String message) {
        if (requestData == null || requestData == "")
            throw new EntityFieldEmptyException(message);
        return requestData;
    }

    S toEntity(T t);

    V toResponse(S s);
}

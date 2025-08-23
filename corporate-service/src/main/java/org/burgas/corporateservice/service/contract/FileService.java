package org.burgas.corporateservice.service.contract;

import jakarta.servlet.http.Part;
import org.burgas.corporateservice.entity.File;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface FileService<T extends File> {

    T findById(final UUID uuid);

    T upload(final Part part);

    void change(final UUID uuid, final Part part);

    void delete(final UUID uuid);
}

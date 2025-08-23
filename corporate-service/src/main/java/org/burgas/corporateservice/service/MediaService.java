package org.burgas.corporateservice.service;

import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.burgas.corporateservice.entity.Media;
import org.burgas.corporateservice.exception.MediaNotFoundException;
import org.burgas.corporateservice.message.MediaMessages;
import org.burgas.corporateservice.repository.MediaRepository;
import org.burgas.corporateservice.service.contract.FileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class MediaService implements FileService<Media> {

    private final MediaRepository mediaRepository;

    @Override
    public Media findById(UUID uuid) {
        return this.mediaRepository.findById(uuid == null ? UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) : uuid)
                .orElseThrow(() -> new MediaNotFoundException(MediaMessages.MEDIA_NOT_FOUND.getMessage()));
    }

    @Override
    @SneakyThrows
    public Media upload(Part part) {
        Media media = Media.builder()
                .name(part.getSubmittedFileName())
                .contentType(part.getContentType())
                .format(Objects.requireNonNull(part.getContentType()).split("/")[1])
                .size(part.getSize())
                .data(part.getInputStream().readAllBytes())
                .build();
        return this.mediaRepository.save(media);
    }

    @Override
    @SneakyThrows
    public void change(UUID uuid, Part part) {
        Media media = this.findById(uuid);
        media.setId(media.getId());
        media.setName(part.getSubmittedFileName());
        media.setContentType(part.getContentType());
        media.setFormat(Objects.requireNonNull(part.getContentType()).split("/")[1]);
        media.setSize(part.getSize());
        media.setData(part.getInputStream().readAllBytes());
        this.mediaRepository.save(media);
    }

    @Override
    public void delete(UUID uuid) {
        Media media = this.findById(uuid);
        this.mediaRepository.delete(media);
    }
}

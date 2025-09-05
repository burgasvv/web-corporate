package org.burgas.corporateservice.service;

import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.corporation.CorporationRequest;
import org.burgas.corporateservice.dto.corporation.CorporationWithOfficesResponse;
import org.burgas.corporateservice.entity.Corporation;
import org.burgas.corporateservice.entity.Media;
import org.burgas.corporateservice.exception.CorporationNotFoundException;
import org.burgas.corporateservice.exception.MediaNotFoundException;
import org.burgas.corporateservice.exception.WrongDirectorIdException;
import org.burgas.corporateservice.mapper.CorporationMapper;
import org.burgas.corporateservice.repository.CorporationRepository;
import org.burgas.corporateservice.service.contract.CrudService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.burgas.corporateservice.message.CorporationMessages.*;
import static org.burgas.corporateservice.message.MediaMessages.MEDIA_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class CorporationService implements CrudService<CorporationRequest, CorporationWithOfficesResponse> {

    private final CorporationRepository corporationRepository;
    private final CorporationMapper corporationMapper;
    private final MediaService mediaService;

    public Corporation findCorporation(final UUID corporationId) {
        return this.corporationRepository.findById(corporationId == null ? UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) : corporationId)
                .orElseThrow(() -> new CorporationNotFoundException(CORPORATION_NOT_FOUND.getMessage()));
    }

    @Override
    public List<CorporationWithOfficesResponse> findAll() {
        return this.corporationRepository.findAll()
                .stream()
                .map(this.corporationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CorporationWithOfficesResponse findById(UUID uuid) {
        return this.corporationMapper.toResponse(this.findCorporation(uuid));
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public CorporationWithOfficesResponse createOrUpdate(CorporationRequest corporationRequest) {
        return this.corporationMapper.toResponse(
                this.corporationRepository.save(this.corporationMapper.toEntity(corporationRequest))
        );
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public String delete(UUID uuid) {
        Corporation corporation = this.findCorporation(uuid);
        this.corporationRepository.deleteCorporationById(corporation.getId());
        return CORPORATION_DELETED.getMessage();
    }

    @Transactional(
            isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public String addDirector(final UUID corporationId, final UUID alreadyDirectorId, final UUID newDirectorId) {
        Corporation corporation = this.findCorporation(corporationId);
        if (corporation.getDirectors().contains(alreadyDirectorId)) {
            corporation.getDirectors().add(newDirectorId);
            this.corporationRepository.save(corporation);
            return CORPORATION_DIRECTOR_ADDED.getMessage();

        } else {
            throw new WrongDirectorIdException(CORPORATION_WRONG_DIRECTOR.getMessage());
        }
    }

    @Transactional(
            isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public String uploadImage(final UUID corporationId, final Part file) {
        Corporation corporation = this.findCorporation(corporationId);
        Media image = this.mediaService.upload(file);
        corporation.setImage(image);
        this.corporationRepository.save(corporation);
        return CORPORATION_IMAGE_UPLOADED.getMessage();
    }

    @Transactional(
            isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public String changeImage(final UUID corporationId, final Part file) {
        Corporation corporation = this.findCorporation(corporationId);
        return Optional.of(corporation.getImage())
                .map(
                        image -> {
                            this.mediaService.change(image.getId(), file);
                            return CORPORATION_IMAGE_CHANGED.getMessage();
                        }
                )
                .orElseThrow(() -> new MediaNotFoundException(MEDIA_NOT_FOUND.getMessage()));
    }

    @Transactional(
            isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public String deleteImage(final UUID corporationId) {
        Corporation corporation = this.findCorporation(corporationId);
        return Optional.of(corporation.getImage())
                .map(
                        image -> {
                            corporation.setImage(null);
                            this.corporationRepository.save(corporation);
                            this.mediaService.delete(image.getId());
                            return CORPORATION_IMAGE_DELETED.getMessage();
                        }
                )
                .orElseThrow(() -> new MediaNotFoundException(MEDIA_NOT_FOUND.getMessage()));
    }
}

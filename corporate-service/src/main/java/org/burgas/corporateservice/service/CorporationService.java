package org.burgas.corporateservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.corporation.CorporationRequest;
import org.burgas.corporateservice.dto.corporation.CorporationResponse;
import org.burgas.corporateservice.entity.Corporation;
import org.burgas.corporateservice.exception.CorporationNotFoundException;
import org.burgas.corporateservice.exception.WrongDirectorIdException;
import org.burgas.corporateservice.mapper.CorporationMapper;
import org.burgas.corporateservice.repository.CorporationRepository;
import org.burgas.corporateservice.service.contract.CrudService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.burgas.corporateservice.message.CorporationMessages.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class CorporationService implements CrudService<CorporationRequest, CorporationResponse> {

    private final CorporationRepository corporationRepository;
    private final CorporationMapper corporationMapper;

    public Corporation findCorporation(final UUID corporationId) {
        return this.corporationRepository.findById(corporationId == null ? UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) : corporationId)
                .orElseThrow(() -> new CorporationNotFoundException(CORPORATION_NOT_FOUND.getMessage()));
    }

    @Override
    public List<CorporationResponse> findAll() {
        return this.corporationRepository.findAll()
                .stream()
                .map(this.corporationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Async(value = "asyncExecutor")
    public CompletableFuture<List<CorporationResponse>> findAllAsync() {
        return CompletableFuture.supplyAsync(this.corporationRepository::findAll)
                .thenApplyAsync(
                        corporations -> corporations
                                .stream()
                                .map(this.corporationMapper::toResponse)
                                .collect(Collectors.toList())
                );
    }

    @Override
    public CorporationResponse findById(UUID uuid) {
        return this.corporationMapper.toResponse(this.findCorporation(uuid));
    }

    @Async(value = "asyncExecutor")
    public CompletableFuture<CorporationResponse> findByIdAsync(final UUID corporationId) {
        return CompletableFuture.supplyAsync(() -> this.findCorporation(corporationId))
                .thenApplyAsync(this.corporationMapper::toResponse);
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public CorporationResponse createOrUpdate(CorporationRequest corporationRequest) {
        return this.corporationMapper.toResponse(
                this.corporationRepository.save(this.corporationMapper.toEntity(corporationRequest))
        );
    }

    @Async(value = "asyncExecutor")
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public CompletableFuture<CorporationResponse> createOrUpdateAsync(final CorporationRequest corporationRequest) {
        return CompletableFuture.supplyAsync(() -> this.corporationMapper.toEntity(corporationRequest))
                .thenApplyAsync(this.corporationRepository::save)
                .thenApplyAsync(this.corporationMapper::toResponse);
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public String delete(UUID uuid) {
        Corporation corporation = this.findCorporation(uuid);
        this.corporationRepository.delete(corporation);
        return CORPORATION_DELETED.getMessage();
    }

    @Async(value = "asyncExecutor")
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public CompletableFuture<String> deleteAsync(final UUID corporationId) {
        return CompletableFuture.supplyAsync(() -> this.delete(corporationId));
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

    @Async(value = "asyncExecutor")
    @Transactional(
            isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public CompletableFuture<String> addDirectorAsync(final UUID corporationId, final UUID alreadyDirectorId, final UUID newDirectorId) {
        return CompletableFuture.supplyAsync(() -> this.addDirector(corporationId, alreadyDirectorId, newDirectorId));
    }
}

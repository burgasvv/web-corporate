package org.burgas.corporateservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.office.OfficeRequest;
import org.burgas.corporateservice.dto.office.OfficeWithEmployeesResponse;
import org.burgas.corporateservice.entity.Office;
import org.burgas.corporateservice.entity.OfficePK;
import org.burgas.corporateservice.exception.OfficeNotFoundException;
import org.burgas.corporateservice.mapper.OfficeMapper;
import org.burgas.corporateservice.message.OfficeMessages;
import org.burgas.corporateservice.repository.OfficeRepository;
import org.burgas.corporateservice.service.contract.OfficeService;
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

import static org.burgas.corporateservice.message.OfficeMessages.OFFICE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class OfficeServiceImpl implements OfficeService<OfficeRequest, OfficeWithEmployeesResponse> {

    private final OfficeRepository officeRepository;
    private final OfficeMapper officeMapper;

    public Office findOffice(final OfficePK officePK) {
        return this.officeRepository.findById(officePK == null ? new OfficePK() : officePK)
                .orElseThrow(() -> new OfficeNotFoundException(OFFICE_NOT_FOUND.getMessage()));
    }

    @Override
    public List<OfficeWithEmployeesResponse> findByCorporationId(UUID corporationId) {
        return this.officeRepository.findOfficesByCorporationId(
                        corporationId == null ? UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) : corporationId
                )
                .stream()
                .map(this.officeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Async(value = "asyncExecutor")
    public CompletableFuture<List<OfficeWithEmployeesResponse>> findByCorporationIdAsync(final UUID corporationId) {
        return CompletableFuture.supplyAsync(() -> this.findByCorporationId(corporationId));
    }

    @Override
    public OfficeWithEmployeesResponse findById(OfficePK officePK) {
        return this.officeMapper.toResponse(this.findOffice(officePK));
    }

    @Async(value = "asyncExecutor")
    public CompletableFuture<OfficeWithEmployeesResponse> findByIdAsync(final OfficePK officePK) {
        return CompletableFuture.supplyAsync(
                () -> this.officeMapper.toResponse(this.findOffice(officePK))
        );
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public OfficeWithEmployeesResponse createOrUpdate(OfficeRequest officeRequest) {
        return this.officeMapper.toResponse(
                this.officeRepository.save(this.officeMapper.toEntity(officeRequest))
        );
    }

    @Async(value = "asyncExecutor")
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public CompletableFuture<OfficeWithEmployeesResponse> createOrUpdateAsync(final OfficeRequest officeRequest) {
        return CompletableFuture.supplyAsync(() -> this.officeMapper.toEntity(officeRequest))
                .thenApplyAsync(this.officeRepository::save)
                .thenApplyAsync(this.officeMapper::toResponse);
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public String delete(OfficePK officePK) {
        Office office = this.findOffice(officePK);
        this.officeRepository.delete(office);
        return OfficeMessages.OFFICE_DELETED.getMessage();
    }

    @Async(value = "asyncExecutor")
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public CompletableFuture<String> deleteAsync(final OfficePK officePK) {
        return CompletableFuture.supplyAsync(() -> this.delete(officePK));
    }
}

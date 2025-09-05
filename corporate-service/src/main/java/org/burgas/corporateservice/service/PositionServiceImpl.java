package org.burgas.corporateservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.position.PositionRequest;
import org.burgas.corporateservice.dto.position.PositionWithEmployeeResponse;
import org.burgas.corporateservice.entity.Department;
import org.burgas.corporateservice.entity.Position;
import org.burgas.corporateservice.exception.PositionNotFoundException;
import org.burgas.corporateservice.mapper.PositionMapper;
import org.burgas.corporateservice.repository.PositionRepository;
import org.burgas.corporateservice.service.contract.PositionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.burgas.corporateservice.message.PositionMessages.POSITION_DELETED;
import static org.burgas.corporateservice.message.PositionMessages.POSITION_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class PositionServiceImpl implements PositionService<PositionRequest, PositionWithEmployeeResponse> {

    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;
    private final DepartmentServiceImpl departmentService;

    public Position findPosition(final UUID positionId) {
        return this.positionRepository.findById(
                        positionId == null ? UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) : positionId
                )
                .orElseThrow(
                        () -> new PositionNotFoundException(POSITION_NOT_FOUND.getMessage())
                );
    }

    @Override
    public List<PositionWithEmployeeResponse> findByDepartmentId(UUID departmentId) {
        Department department = this.departmentService.findDepartment(departmentId);
        return this.positionRepository.findPositionsByDepartment(department)
                .stream()
                .map(this.positionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PositionWithEmployeeResponse> findByCorporationId(UUID corporationId) {
        return this.positionRepository.findPositionsByCorporationId(
                        corporationId == null ? UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) : corporationId
                )
                .stream()
                .map(this.positionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PositionWithEmployeeResponse findById(UUID positionId) {
        return this.positionMapper.toResponse(this.findPosition(positionId));
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public PositionWithEmployeeResponse createOrUpdate(PositionRequest positionRequest) {
        return this.positionMapper.toResponse(
                this.positionRepository.save(this.positionMapper.toEntity(positionRequest))
        );
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public String delete(UUID positionId) {
        Position position = this.findPosition(positionId);
        this.positionRepository.deletePositionById(position.getId());
        return POSITION_DELETED.getMessage();
    }
}

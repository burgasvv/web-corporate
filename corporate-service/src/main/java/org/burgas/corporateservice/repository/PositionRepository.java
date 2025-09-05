package org.burgas.corporateservice.repository;

import org.burgas.corporateservice.entity.Department;
import org.burgas.corporateservice.entity.Position;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PositionRepository extends JpaRepository<Position, UUID> {

    @EntityGraph(value = "position-entity-graph", type = EntityGraph.EntityGraphType.LOAD)
    List<Position> findPositionsByDepartment(Department department);

    @Query(
            nativeQuery = true,
            value = """
                    select p.* from position p
                        join public.department d on d.id = p.department_id
                        join public.corporation c on c.id = d.corporation_id
                        where c.id = :corporationId
                    """
    )
    List<Position> findPositionsByCorporationId(final UUID corporationId);

    @Override
    @EntityGraph(value = "position-entity-graph", type = EntityGraph.EntityGraphType.LOAD)
    @NotNull Optional<Position> findById(@NotNull UUID uuid);

    @Modifying
    @Query(
            nativeQuery = true,
            value = "delete from position p where p.id = :positionId"
    )
    void deletePositionById(UUID positionId);
}

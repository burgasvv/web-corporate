package org.burgas.corporateservice.repository;

import org.burgas.corporateservice.entity.Office;
import org.burgas.corporateservice.entity.OfficePK;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OfficeRepository extends JpaRepository<Office, OfficePK> {

    @Override
    @EntityGraph(value = "office-entity-graph", type = EntityGraph.EntityGraphType.LOAD)
    @NotNull Optional<Office> findById(@NotNull OfficePK officePK);

    @Query(
            nativeQuery = true,
            value = "select o.* from office o where o.corporation_id = :corporationId"
    )
    List<Office> findOfficesByCorporationId(final UUID corporationId);
}

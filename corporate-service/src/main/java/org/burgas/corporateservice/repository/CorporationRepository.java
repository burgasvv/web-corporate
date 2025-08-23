package org.burgas.corporateservice.repository;

import org.burgas.corporateservice.entity.Corporation;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CorporationRepository extends JpaRepository<Corporation, UUID> {

    @Override
    @EntityGraph(value = "corporation-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NotNull List<Corporation> findAll();

    @Override
    @EntityGraph(value = "corporation-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NotNull Optional<Corporation> findById(@NotNull UUID uuid);
}

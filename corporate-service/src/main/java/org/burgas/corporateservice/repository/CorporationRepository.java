package org.burgas.corporateservice.repository;

import org.burgas.corporateservice.entity.Corporation;
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
public interface CorporationRepository extends JpaRepository<Corporation, UUID> {

    @Override
    @Query(value = "select c from org.burgas.corporateservice.entity.Corporation c join fetch c.departments")
    @NotNull List<Corporation> findAll();

    @Override
    @EntityGraph(value = "corporation-entity-graph", type = EntityGraph.EntityGraphType.LOAD)
    @NotNull Optional<Corporation> findById(@NotNull UUID uuid);

    @Modifying
    @Query(
            nativeQuery = true,
            value = "delete from corporation c where c.id = :corporationId"
    )
    void deleteCorporationById(UUID corporationId);
}

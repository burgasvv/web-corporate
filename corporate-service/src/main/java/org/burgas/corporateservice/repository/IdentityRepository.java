package org.burgas.corporateservice.repository;

import org.burgas.corporateservice.entity.Identity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IdentityRepository extends JpaRepository<Identity, UUID> {

    @Override
    @EntityGraph(value = "identity-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NotNull List<Identity> findAll();

    @Override
    @EntityGraph(value = "identity-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NotNull Optional<Identity> findById(@NotNull UUID uuid);

    @EntityGraph(value = "identity-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    Optional<Identity> findIdentityByEmail(String email);
}

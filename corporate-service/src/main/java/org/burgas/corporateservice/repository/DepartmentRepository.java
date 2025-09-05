package org.burgas.corporateservice.repository;

import org.burgas.corporateservice.entity.Department;
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
public interface DepartmentRepository extends JpaRepository<Department, UUID> {

    @Query(
            nativeQuery = true,
            value = "select d.* from department d where d.corporation_id = :corporationId"
    )
    List<Department> findDepartmentsByCorporationId(final UUID corporationId);

    @Override
    @EntityGraph(value = "department-entity-graph", type = EntityGraph.EntityGraphType.LOAD)
    @NotNull Optional<Department> findById(@NotNull UUID uuid);

    @Modifying
    @Query(
            nativeQuery = true,
            value = "delete from department d where d.id = :departmentId"
    )
    void deleteDepartmentById(UUID departmentId);
}

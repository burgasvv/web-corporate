package org.burgas.corporateservice.repository;

import org.burgas.corporateservice.entity.Employee;
import org.burgas.corporateservice.entity.Office;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    @Query(
            nativeQuery = true,
            value = """
                    select e.* from employee e
                    join public.office o on o.corporation_id = e.office_corporation_id and o.address_id = e.office_address_id
                    join public.corporation c on c.id = o.corporation_id
                    where c.id = :corporationId
                    """
    )
    List<Employee> findEmployeesByCorporationId(final UUID corporationId);

    @EntityGraph(value = "employee-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    List<Employee> findEmployeesByOffice(Office office);

    @Override
    @EntityGraph(value = "employee-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NotNull Optional<Employee> findById(@NotNull UUID uuid);
}

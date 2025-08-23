package org.burgas.corporateservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NamedEntityGraph(
        name = "office-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "employees", subgraph = "employees-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "employees-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "address")
                        }
                )
        }
)
public final class Office extends AbstractEntity {

    @EmbeddedId
    private OfficePK officePK;

    @Column(name = "employees_amount", nullable = false)
    private Long employeesAmount;

    @OneToMany(mappedBy = "office", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Employee> employees;
}

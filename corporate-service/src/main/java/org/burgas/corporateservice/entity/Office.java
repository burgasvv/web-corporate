package org.burgas.corporateservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "office", schema = "public")
@NamedEntityGraph(
        name = "office-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "employees", subgraph = "employees-subgraph"),
                @NamedAttributeNode(value = "departments", subgraph = "departments-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "employees-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "address")
                        }
                ),
                @NamedSubgraph(
                        name = "departments-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "corporation")
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

    @ManyToMany
    @JoinTable(
            name = "office_department",
            joinColumns = {
                    @JoinColumn(name = "office_corporation_id", referencedColumnName = "corporation_id"),
                    @JoinColumn(name = "office_address_id", referencedColumnName = "address_id")
            },
            inverseJoinColumns = @JoinColumn(name = "department_id", referencedColumnName = "id")
    )
    private List<Department> departments;
}

package org.burgas.corporateservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "corporation", schema = "public")
@NamedEntityGraph(
        name = "corporation-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "image"),
                @NamedAttributeNode(value = "departments", subgraph = "department-subgraph")
        },
        subgraphs = @NamedSubgraph(
                name = "department-subgraph", attributeNodes = @NamedAttributeNode(value = "offices")
        )
)
public final class Corporation extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "description", unique = true, nullable = false)
    private String description;

    @Column(name = "offices_amount")
    private Long officesAmount;

    @Column(name = "employees_amount")
    private Long employeesAmount;

    @JdbcTypeCode(value = SqlTypes.ARRAY)
    @Column(name = "directors", nullable = false)
    private List<UUID> directors;

    @OneToOne(targetEntity = Media.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "image_id", referencedColumnName = "id", unique = true)
    private Media image;

    @OneToMany(
            mappedBy = "corporation", targetEntity = Department.class,
            cascade = CascadeType.ALL, fetch = FetchType.EAGER
    )
    private List<Department> departments;
}

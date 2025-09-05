package org.burgas.corporateservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "position", schema = "public")
@NamedEntityGraph(
        name = "position-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "department"),
                @NamedAttributeNode(value = "employee")
        }
)
public final class Position extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    private Department department;

    @OneToOne(mappedBy = "position", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Employee employee;
}

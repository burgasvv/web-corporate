package org.burgas.corporateservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;



@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "department", schema = "public")
@NamedEntityGraph(
        name = "department-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "offices"),
                @NamedAttributeNode(value = "corporation"),
                @NamedAttributeNode(value = "positions")
        }
)
public final class Department extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToMany(
            mappedBy = "departments", fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH}
    )
    private List<Office> offices;

    public void addOffices(final List<Office> newOffices) {
        this.offices.addAll(newOffices);
        newOffices.forEach(office -> office.getDepartments().add(this));
    }

    @ManyToOne
    @JoinColumn(name = "corporation_id", referencedColumnName = "id")
    private Corporation corporation;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Position> positions;
}

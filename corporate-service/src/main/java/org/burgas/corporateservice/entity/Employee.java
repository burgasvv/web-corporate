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
@Table(name = "employee", schema = "public")
@NamedEntityGraph(
        name = "employee-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "address"),
                @NamedAttributeNode(value = "office"),
                @NamedAttributeNode(value = "identity"),
                @NamedAttributeNode(value = "position", subgraph = "position-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "position-subgraph",
                        attributeNodes = @NamedAttributeNode(value = "department")
                )
        }
)
public final class Employee extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "identity_id", referencedColumnName = "id", unique = true)
    private Identity identity;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "patronymic", nullable = false)
    private String patronymic;

    @Column(name = "about", nullable = false, unique = true)
    private String about;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id", unique = true)
    private Address address;

    @ManyToOne
    @JoinColumns(
            value = {
                    @JoinColumn(name = "office_corporation_id", referencedColumnName = "corporation_id"),
                    @JoinColumn(name = "office_address_id", referencedColumnName = "address_id")
            }
    )
    private Office office;

    @OneToOne
    @JoinColumn(name = "position_id", referencedColumnName = "id")
    private Position position;
}

package org.burgas.corporateservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "employee", schema = "public")
@NamedEntityGraph(
        name = "employee-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "address"),
                @NamedAttributeNode(value = "office"),
                @NamedAttributeNode(value = "identity")
        }
)
public final class Employee extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
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

    @OneToOne(targetEntity = Address.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id", referencedColumnName = "id", unique = true)
    private Address address;

    @ManyToOne(
            targetEntity = Office.class,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH},
            fetch = FetchType.EAGER
    )
    @JoinColumns(
            value = {
                    @JoinColumn(name = "office_corporation_id", referencedColumnName = "corporation_id"),
                    @JoinColumn(name = "office_address_id", referencedColumnName = "address_id")
            }
    )
    private Office office;
}

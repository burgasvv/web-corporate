package org.burgas.corporateservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "identity", schema = "public")
@NamedEntityGraph(
        name = "identity-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "image"),
                @NamedAttributeNode(value = "employee", subgraph = "employee-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "employee-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "address"),
                                @NamedAttributeNode(value = "office")
                        }
                )
        }
)
public final class Identity extends AbstractEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "authority", nullable = false)
    private Authority authority;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "phone", unique = true, nullable = false)
    private String phone;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @OneToOne(mappedBy = "identity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Employee employee;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "image_id", referencedColumnName = "id", unique = true)
    private Media image;

    public String getUsernameNotUserDetails() {
        return this.username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(this.authority);
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return enabled || !UserDetails.super.isEnabled();
    }
}

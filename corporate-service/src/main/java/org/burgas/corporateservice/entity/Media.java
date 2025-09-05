package org.burgas.corporateservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "media", schema = "public")
public final class Media extends File {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "format", nullable = false)
    private String format;

    @Column(name = "size", nullable = false)
    private Long size;

    @JsonIgnore
    @Column(name = "data", nullable = false)
    private byte[] data;
}

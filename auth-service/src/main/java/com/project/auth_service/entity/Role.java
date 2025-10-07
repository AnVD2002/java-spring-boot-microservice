package com.project.auth_service.entity;

import com.project.common_lib_service.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    @SequenceGenerator(name = "role_seq", sequenceName = "role_id_seq", allocationSize = 1)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;
}

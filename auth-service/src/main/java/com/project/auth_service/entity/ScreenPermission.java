package com.project.auth_service.entity;

import com.project.common_lib_service.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serial;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "screen_permissions")
public class ScreenPermission extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @UuidGenerator
    @Column(name = "id")
    private UUID id;

    @Column(name = "screen_id", nullable = false)
    private Long screenId;

    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    @Column(name = "access_type", nullable = false)
    private String accessType;
}

package com.project.auth_service.entity;

import com.project.common_lib_service.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "screens")
public class Screen extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "module", nullable = false)
    private String module;

    @Column(name = "route_path", nullable = false, unique = true)
    private String routePath;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "icon")
    private String icon;

    @Column(name = "is_visible", nullable = false)
    private Boolean visible;

    @Column(name = "description")
    private String description;
}

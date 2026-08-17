package com.project.elearning_service.entity;

import com.project.common_lib_service.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, length = 100)
    private String code;

    @Column(nullable = false)
    private String title;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "thumbnail_url", length = 1000)
    private String thumbnailUrl;

    @Column(nullable = false)
    private Integer status;

    @Column(name = "instructor_id", nullable = false)
    private UUID instructorId;

    @Column(length = 50)
    private String level;

    @Column(length = 20)
    private String language;

    @Column(name = "published_at")
    private Instant publishedAt;
}

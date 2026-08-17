package com.project.elearning_service.entity;

import com.project.common_lib_service.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "lesson_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonDocument extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "lesson_id", nullable = false)
    private UUID lessonId;

    @Column(name = "document_id", nullable = false)
    private UUID documentId;

    @Column(name = "document_version", nullable = false)
    private Integer documentVersion;

    private String title;

    @Column(name = "document_type", length = 50)
    private String documentType;

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(nullable = false)
    private Integer status;
}

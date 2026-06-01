package com.project.testing_service.entity;

import com.project.common_lib_service.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "exam_id", nullable = false)
    private UUID examId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "question_type", nullable = false, length = 50)
    private String questionType;

    @Column(nullable = false)
    private Integer score;
}

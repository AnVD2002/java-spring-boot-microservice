package com.project.elearning_service.repository;

import com.project.elearning_service.entity.LessonDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LessonDocumentRepository extends JpaRepository<LessonDocument, UUID> {
    List<LessonDocument> findByLessonIdOrderByDisplayOrderAsc(UUID lessonId);
}

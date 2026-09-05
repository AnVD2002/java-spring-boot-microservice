package com.project.e_learning_service.repository;

import com.project.e_learning_service.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    List<Lesson> findByCourseIdOrderByOrderIndex(UUID courseId);

    List<Lesson> findByModuleIdOrderByOrderIndexAsc(UUID moduleId);
}

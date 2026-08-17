package com.project.elearning_service.repository;

import com.project.elearning_service.entity.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseModuleRepository extends JpaRepository<CourseModule, UUID> {
    List<CourseModule> findByCourseIdOrderByOrderIndexAsc(UUID courseId);
}

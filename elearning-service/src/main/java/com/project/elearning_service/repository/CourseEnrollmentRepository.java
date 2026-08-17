package com.project.elearning_service.repository;

import com.project.elearning_service.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, UUID> {
    Optional<CourseEnrollment> findByCourseIdAndLearnerId(UUID courseId, UUID learnerId);

    List<CourseEnrollment> findByLearnerId(UUID learnerId);
}

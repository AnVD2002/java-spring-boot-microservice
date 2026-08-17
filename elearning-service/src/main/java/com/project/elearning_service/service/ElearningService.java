package com.project.elearning_service.service;

import com.project.elearning_service.dto.request.*;
import com.project.elearning_service.dto.response.*;

import java.util.List;
import java.util.UUID;

public interface ElearningService {
    CourseResponse createCourse(CreateCourseRequest request, UUID instructorId);

    List<CourseResponse> listCourses();

    ModuleResponse createModule(UUID courseId, CreateModuleRequest request);

    List<ModuleResponse> listModules(UUID courseId);

    LessonResponse createLesson(UUID courseId, CreateLessonRequest request);

    List<LessonResponse> listLessons(UUID courseId);

    LessonDocumentResponse attachDocument(UUID lessonId, AttachLessonDocumentRequest request);

    List<LessonDocumentResponse> listLessonDocuments(UUID lessonId);

    CourseEnrollmentResponse enroll(UUID courseId, UUID learnerId);

    List<CourseEnrollmentResponse> listMyEnrollments(UUID learnerId);

    LessonProgressResponse updateLessonProgress(UUID enrollmentId, UUID learnerId, UpdateLessonProgressRequest request);
}

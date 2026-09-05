package com.project.e_learning_service.service;

import com.project.common_lib_service.dto.CustomUserPrincipal;
import com.project.e_learning_service.dto.request.*;
import com.project.e_learning_service.dto.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ELearningService {
    CourseResponse createCourse(CreateCourseRequest request, UUID instructorId);

    List<CourseResponse> listCourses();

    ModuleResponse createModule(UUID courseId, CreateModuleRequest request);

    List<ModuleResponse> listModules(UUID courseId);

    LessonResponse createLesson(UUID courseId, CreateLessonRequest request);

    List<LessonResponse> listLessons(UUID courseId);

    LessonDocumentResponse attachDocument(UUID lessonId, AttachLessonDocumentRequest request);

    LessonDocumentResponse uploadAndAttachDocument(UUID lessonId, UploadLessonDocumentRequest request, MultipartFile file, CustomUserPrincipal principal);

    List<LessonDocumentResponse> listLessonDocuments(UUID lessonId);

    CourseEnrollmentResponse enroll(UUID courseId, UUID learnerId);

    List<CourseEnrollmentResponse> listMyEnrollments(UUID learnerId);

    LessonProgressResponse updateLessonProgress(UUID enrollmentId, UUID learnerId, UpdateLessonProgressRequest request);
}

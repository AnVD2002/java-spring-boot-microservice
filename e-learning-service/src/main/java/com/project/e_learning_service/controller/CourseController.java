package com.project.e_learning_service.controller;

import com.project.common_lib_service.dto.ResponseData;
import com.project.common_lib_service.dto.CustomUserPrincipal;
import com.project.common_lib_service.utils.ResponseUtils;
import com.project.e_learning_service.dto.request.*;
import com.project.e_learning_service.dto.response.*;
import com.project.e_learning_service.service.ELearningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/eLearning")
@RequiredArgsConstructor
public class CourseController {

    private final ELearningService eLearningService;

    @GetMapping("/health")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<ResponseData<String>> health() {
        return ResponseUtils.success("e-learning-service is running");
    }

    @PostMapping("/courses")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER')")
    public ResponseEntity<ResponseData<CourseResponse>> createCourse(
            @Valid @RequestBody CreateCourseRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseUtils.success(eLearningService.createCourse(request, principal.getUserId()));
    }

    @GetMapping("/courses")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER', 'USER')")
    public ResponseEntity<ResponseData<List<CourseResponse>>> listCourses() {
        return ResponseUtils.success(eLearningService.listCourses());
    }

    @PostMapping("/courses/{courseId}/modules")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER')")
    public ResponseEntity<ResponseData<ModuleResponse>> createModule(
            @PathVariable UUID courseId,
            @Valid @RequestBody CreateModuleRequest request
    ) {
        return ResponseUtils.success(eLearningService.createModule(courseId, request));
    }

    @GetMapping("/courses/{courseId}/modules")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER', 'USER')")
    public ResponseEntity<ResponseData<List<ModuleResponse>>> listModules(@PathVariable UUID courseId) {
        return ResponseUtils.success(eLearningService.listModules(courseId));
    }

    @PostMapping("/courses/{courseId}/lessons")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER')")
    public ResponseEntity<ResponseData<LessonResponse>> createLesson(
            @PathVariable UUID courseId,
            @Valid @RequestBody CreateLessonRequest request
    ) {
        return ResponseUtils.success(eLearningService.createLesson(courseId, request));
    }

    @GetMapping("/courses/{courseId}/lessons")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER', 'USER')")
    public ResponseEntity<ResponseData<List<LessonResponse>>> listLessons(@PathVariable UUID courseId) {
        return ResponseUtils.success(eLearningService.listLessons(courseId));
    }

    @PostMapping("/lessons/{lessonId}/documents")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER')")
    public ResponseEntity<ResponseData<LessonDocumentResponse>> attachDocument(
            @PathVariable UUID lessonId,
            @Valid @RequestBody AttachLessonDocumentRequest request
    ) {
        return ResponseUtils.success(eLearningService.attachDocument(lessonId, request));
    }

    @PostMapping(value = "/lessons/{lessonId}/documents/upload", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER')")
    public ResponseEntity<ResponseData<LessonDocumentResponse>> uploadAndAttachDocument(
            @PathVariable UUID lessonId,
            @Valid @ModelAttribute UploadLessonDocumentRequest request,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseUtils.success(eLearningService.uploadAndAttachDocument(lessonId, request, file, principal));
    }

    @GetMapping("/lessons/{lessonId}/documents")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER', 'USER')")
    public ResponseEntity<ResponseData<List<LessonDocumentResponse>>> listLessonDocuments(@PathVariable UUID lessonId) {
        return ResponseUtils.success(eLearningService.listLessonDocuments(lessonId));
    }

    @PostMapping("/courses/{courseId}/enrollments")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER', 'USER')")
    public ResponseEntity<ResponseData<CourseEnrollmentResponse>> enroll(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseUtils.success(eLearningService.enroll(courseId, principal.getUserId()));
    }

    @GetMapping("/me/enrollments")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER', 'USER')")
    public ResponseEntity<ResponseData<List<CourseEnrollmentResponse>>> listMyEnrollments(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseUtils.success(eLearningService.listMyEnrollments(principal.getUserId()));
    }

    @PutMapping("/enrollments/{enrollmentId}/progress")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER', 'USER')")
    public ResponseEntity<ResponseData<LessonProgressResponse>> updateProgress(
            @PathVariable UUID enrollmentId,
            @Valid @RequestBody UpdateLessonProgressRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseUtils.success(eLearningService.updateLessonProgress(enrollmentId, principal.getUserId(), request));
    }
}

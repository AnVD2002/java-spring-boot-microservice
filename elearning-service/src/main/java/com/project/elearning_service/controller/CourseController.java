package com.project.elearning_service.controller;

import com.project.common_lib_service.dto.ResponseData;
import com.project.common_lib_service.dto.CustomUserPrincipal;
import com.project.common_lib_service.utils.ResponseUtils;
import com.project.elearning_service.dto.request.*;
import com.project.elearning_service.dto.response.*;
import com.project.elearning_service.service.ElearningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/elearning")
@RequiredArgsConstructor
public class CourseController {

    private final ElearningService elearningService;

    @GetMapping("/health")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<ResponseData<String>> health() {
        return ResponseUtils.success("elearning-service is running");
    }

    @PostMapping("/courses")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER')")
    public ResponseEntity<ResponseData<CourseResponse>> createCourse(
            @Valid @RequestBody CreateCourseRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseUtils.success(elearningService.createCourse(request, principal.getUserId()));
    }

    @GetMapping("/courses")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER', 'USER')")
    public ResponseEntity<ResponseData<List<CourseResponse>>> listCourses() {
        return ResponseUtils.success(elearningService.listCourses());
    }

    @PostMapping("/courses/{courseId}/modules")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER')")
    public ResponseEntity<ResponseData<ModuleResponse>> createModule(
            @PathVariable UUID courseId,
            @Valid @RequestBody CreateModuleRequest request
    ) {
        return ResponseUtils.success(elearningService.createModule(courseId, request));
    }

    @GetMapping("/courses/{courseId}/modules")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER', 'USER')")
    public ResponseEntity<ResponseData<List<ModuleResponse>>> listModules(@PathVariable UUID courseId) {
        return ResponseUtils.success(elearningService.listModules(courseId));
    }

    @PostMapping("/courses/{courseId}/lessons")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER')")
    public ResponseEntity<ResponseData<LessonResponse>> createLesson(
            @PathVariable UUID courseId,
            @Valid @RequestBody CreateLessonRequest request
    ) {
        return ResponseUtils.success(elearningService.createLesson(courseId, request));
    }

    @GetMapping("/courses/{courseId}/lessons")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER', 'USER')")
    public ResponseEntity<ResponseData<List<LessonResponse>>> listLessons(@PathVariable UUID courseId) {
        return ResponseUtils.success(elearningService.listLessons(courseId));
    }

    @PostMapping("/lessons/{lessonId}/documents")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER')")
    public ResponseEntity<ResponseData<LessonDocumentResponse>> attachDocument(
            @PathVariable UUID lessonId,
            @Valid @RequestBody AttachLessonDocumentRequest request
    ) {
        return ResponseUtils.success(elearningService.attachDocument(lessonId, request));
    }

    @GetMapping("/lessons/{lessonId}/documents")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER', 'USER')")
    public ResponseEntity<ResponseData<List<LessonDocumentResponse>>> listLessonDocuments(@PathVariable UUID lessonId) {
        return ResponseUtils.success(elearningService.listLessonDocuments(lessonId));
    }

    @PostMapping("/courses/{courseId}/enrollments")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER', 'USER')")
    public ResponseEntity<ResponseData<CourseEnrollmentResponse>> enroll(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseUtils.success(elearningService.enroll(courseId, principal.getUserId()));
    }

    @GetMapping("/me/enrollments")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER', 'USER')")
    public ResponseEntity<ResponseData<List<CourseEnrollmentResponse>>> listMyEnrollments(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseUtils.success(elearningService.listMyEnrollments(principal.getUserId()));
    }

    @PutMapping("/enrollments/{enrollmentId}/progress")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER', 'USER')")
    public ResponseEntity<ResponseData<LessonProgressResponse>> updateProgress(
            @PathVariable UUID enrollmentId,
            @Valid @RequestBody UpdateLessonProgressRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseUtils.success(elearningService.updateLessonProgress(enrollmentId, principal.getUserId(), request));
    }
}

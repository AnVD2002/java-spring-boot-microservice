package com.project.elearning_service.service.impl;

import com.project.elearning_service.dto.request.*;
import com.project.elearning_service.dto.response.*;
import com.project.elearning_service.entity.*;
import com.project.elearning_service.repository.*;
import com.project.elearning_service.service.ElearningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ElearningServiceImpl implements ElearningService {

    private static final int STATUS_DRAFT = 0;
    private static final int STATUS_ACTIVE = 1;
    private static final int STATUS_COMPLETED = 2;

    private final CourseRepository courseRepository;
    private final CourseModuleRepository courseModuleRepository;
    private final LessonRepository lessonRepository;
    private final LessonDocumentRepository lessonDocumentRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;

    @Override
    public CourseResponse createCourse(CreateCourseRequest request, UUID instructorId) {
        Course course = Course.builder()
                .code(request.getCode())
                .title(request.getTitle())
                .shortDescription(request.getShortDescription())
                .description(request.getDescription())
                .thumbnailUrl(request.getThumbnailUrl())
                .level(request.getLevel())
                .language(request.getLanguage() == null ? "vi" : request.getLanguage())
                .status(request.getStatus() == null ? STATUS_DRAFT : request.getStatus())
                .instructorId(instructorId)
                .build();
        return toCourseResponse(courseRepository.save(course));
    }

    @Override
    public List<CourseResponse> listCourses() {
        return courseRepository.findAll().stream().map(this::toCourseResponse).toList();
    }

    @Override
    public ModuleResponse createModule(UUID courseId, CreateModuleRequest request) {
        ensureCourseExists(courseId);
        CourseModule module = CourseModule.builder()
                .courseId(courseId)
                .title(request.getTitle())
                .description(request.getDescription())
                .orderIndex(request.getOrderIndex() == null ? 0 : request.getOrderIndex())
                .status(request.getStatus() == null ? STATUS_ACTIVE : request.getStatus())
                .build();
        return toModuleResponse(courseModuleRepository.save(module));
    }

    @Override
    public List<ModuleResponse> listModules(UUID courseId) {
        ensureCourseExists(courseId);
        return courseModuleRepository.findByCourseIdOrderByOrderIndexAsc(courseId)
                .stream().map(this::toModuleResponse).toList();
    }

    @Override
    public LessonResponse createLesson(UUID courseId, CreateLessonRequest request) {
        ensureCourseExists(courseId);
        if (request.getModuleId() != null) {
            CourseModule module = findModule(request.getModuleId());
            if (!module.getCourseId().equals(courseId)) {
                throw new IllegalArgumentException("Module does not belong to course: " + courseId);
            }
        }

        Lesson lesson = Lesson.builder()
                .courseId(courseId)
                .moduleId(request.getModuleId())
                .title(request.getTitle())
                .summary(request.getSummary())
                .lessonType(request.getLessonType() == null ? "DOCUMENT" : request.getLessonType())
                .contentUrl(request.getContentUrl())
                .orderIndex(request.getOrderIndex() == null ? 0 : request.getOrderIndex())
                .durationMinutes(request.getDurationMinutes() == null ? 0 : request.getDurationMinutes())
                .isPreview(request.getIsPreview() != null && request.getIsPreview())
                .status(request.getStatus() == null ? STATUS_DRAFT : request.getStatus())
                .build();
        return toLessonResponse(lessonRepository.save(lesson));
    }

    @Override
    public List<LessonResponse> listLessons(UUID courseId) {
        ensureCourseExists(courseId);
        return lessonRepository.findByCourseIdOrderByOrderIndex(courseId)
                .stream().map(this::toLessonResponse).toList();
    }

    @Override
    public LessonDocumentResponse attachDocument(UUID lessonId, AttachLessonDocumentRequest request) {
        ensureLessonExists(lessonId);
        LessonDocument document = LessonDocument.builder()
                .lessonId(lessonId)
                .documentId(request.getDocumentId())
                .documentVersion(request.getDocumentVersion() == null ? 1 : request.getDocumentVersion())
                .title(request.getTitle())
                .documentType(request.getDocumentType())
                .isRequired(request.getIsRequired() == null || request.getIsRequired())
                .displayOrder(request.getDisplayOrder() == null ? 0 : request.getDisplayOrder())
                .status(request.getStatus() == null ? STATUS_ACTIVE : request.getStatus())
                .build();
        return toLessonDocumentResponse(lessonDocumentRepository.save(document));
    }

    @Override
    public List<LessonDocumentResponse> listLessonDocuments(UUID lessonId) {
        ensureLessonExists(lessonId);
        return lessonDocumentRepository.findByLessonIdOrderByDisplayOrderAsc(lessonId)
                .stream().map(this::toLessonDocumentResponse).toList();
    }

    @Override
    public CourseEnrollmentResponse enroll(UUID courseId, UUID learnerId) {
        ensureCourseExists(courseId);
        CourseEnrollment enrollment = courseEnrollmentRepository.findByCourseIdAndLearnerId(courseId, learnerId)
                .orElseGet(() -> CourseEnrollment.builder()
                        .courseId(courseId)
                        .learnerId(learnerId)
                        .status(STATUS_DRAFT)
                        .progressPercent(BigDecimal.ZERO)
                        .enrolledAt(Instant.now())
                        .build());
        return toEnrollmentResponse(courseEnrollmentRepository.save(enrollment));
    }

    @Override
    public List<CourseEnrollmentResponse> listMyEnrollments(UUID learnerId) {
        return courseEnrollmentRepository.findByLearnerId(learnerId)
                .stream().map(this::toEnrollmentResponse).toList();
    }

    @Override
    @Transactional
    public LessonProgressResponse updateLessonProgress(UUID enrollmentId, UUID learnerId, UpdateLessonProgressRequest request) {
        CourseEnrollment enrollment = courseEnrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found: " + enrollmentId));
        if (!enrollment.getLearnerId().equals(learnerId)) {
            throw new IllegalArgumentException("Enrollment does not belong to current learner");
        }
        Lesson lesson = findLesson(request.getLessonId());
        if (!lesson.getCourseId().equals(enrollment.getCourseId())) {
            throw new IllegalArgumentException("Lesson does not belong to enrollment course");
        }

        Instant now = Instant.now();
        LessonProgress progress = lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollmentId, request.getLessonId())
                .orElseGet(() -> LessonProgress.builder()
                        .enrollmentId(enrollmentId)
                        .lessonId(request.getLessonId())
                        .learnerId(learnerId)
                        .status(STATUS_ACTIVE)
                        .progressPercent(BigDecimal.ZERO)
                        .lastPositionSeconds(0)
                        .timeSpentSeconds(0)
                        .startedAt(now)
                        .build());

        BigDecimal percent = request.getProgressPercent() == null ? progress.getProgressPercent() : request.getProgressPercent();
        int status = request.getStatus() == null ? progress.getStatus() : request.getStatus();
        if (percent.compareTo(BigDecimal.valueOf(100)) >= 0) {
            status = STATUS_COMPLETED;
            progress.setCompletedAt(now);
        }
        progress.setProgressPercent(percent);
        progress.setStatus(status);
        progress.setLastPositionSeconds(request.getLastPositionSeconds() == null ? progress.getLastPositionSeconds() : request.getLastPositionSeconds());
        progress.setTimeSpentSeconds(request.getTimeSpentSeconds() == null ? progress.getTimeSpentSeconds() : request.getTimeSpentSeconds());
        progress.setLastAccessedAt(now);

        LessonProgress saved = lessonProgressRepository.save(progress);
        recalculateEnrollmentProgress(enrollment);
        return toLessonProgressResponse(saved);
    }

    private void recalculateEnrollmentProgress(CourseEnrollment enrollment) {
        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByOrderIndex(enrollment.getCourseId());
        if (lessons.isEmpty()) {
            return;
        }
        List<LessonProgress> progressList = lessonProgressRepository.findByEnrollmentId(enrollment.getId());
        long completed = progressList.stream().filter(progress -> STATUS_COMPLETED == progress.getStatus()).count();
        BigDecimal percent = BigDecimal.valueOf(completed)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(lessons.size()), 2, RoundingMode.HALF_UP);
        enrollment.setProgressPercent(percent);
        enrollment.setStatus(completed == lessons.size() ? STATUS_COMPLETED : STATUS_ACTIVE);
        if (enrollment.getStartedAt() == null) {
            enrollment.setStartedAt(Instant.now());
        }
        if (completed == lessons.size()) {
            enrollment.setCompletedAt(Instant.now());
        }
        courseEnrollmentRepository.save(enrollment);
    }

    private void ensureCourseExists(UUID courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new IllegalArgumentException("Course not found: " + courseId);
        }
    }

    private void ensureModuleExists(UUID moduleId) {
        if (!courseModuleRepository.existsById(moduleId)) {
            throw new IllegalArgumentException("Module not found: " + moduleId);
        }
    }

    private CourseModule findModule(UUID moduleId) {
        return courseModuleRepository.findById(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("Module not found: " + moduleId));
    }

    private void ensureLessonExists(UUID lessonId) {
        if (!lessonRepository.existsById(lessonId)) {
            throw new IllegalArgumentException("Lesson not found: " + lessonId);
        }
    }

    private Lesson findLesson(UUID lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found: " + lessonId));
    }

    private CourseResponse toCourseResponse(Course course) {
        return new CourseResponse(course.getId(), course.getCode(), course.getTitle(), course.getShortDescription(),
                course.getDescription(), course.getThumbnailUrl(), course.getLevel(), course.getLanguage(),
                course.getStatus(), course.getInstructorId(), course.getPublishedAt());
    }

    private ModuleResponse toModuleResponse(CourseModule module) {
        return new ModuleResponse(module.getId(), module.getCourseId(), module.getTitle(),
                module.getDescription(), module.getOrderIndex(), module.getStatus());
    }

    private LessonResponse toLessonResponse(Lesson lesson) {
        return new LessonResponse(lesson.getId(), lesson.getCourseId(), lesson.getModuleId(), lesson.getTitle(),
                lesson.getSummary(), lesson.getLessonType(), lesson.getContentUrl(), lesson.getOrderIndex(),
                lesson.getDurationMinutes(), lesson.getIsPreview(), lesson.getStatus());
    }

    private LessonDocumentResponse toLessonDocumentResponse(LessonDocument document) {
        return new LessonDocumentResponse(document.getId(), document.getLessonId(), document.getDocumentId(),
                document.getDocumentVersion(), document.getTitle(), document.getDocumentType(),
                document.getIsRequired(), document.getDisplayOrder(), document.getStatus());
    }

    private CourseEnrollmentResponse toEnrollmentResponse(CourseEnrollment enrollment) {
        return new CourseEnrollmentResponse(enrollment.getId(), enrollment.getCourseId(), enrollment.getLearnerId(),
                enrollment.getStatus(), enrollment.getProgressPercent(), enrollment.getEnrolledAt(),
                enrollment.getStartedAt(), enrollment.getCompletedAt());
    }

    private LessonProgressResponse toLessonProgressResponse(LessonProgress progress) {
        return new LessonProgressResponse(progress.getId(), progress.getEnrollmentId(), progress.getLessonId(),
                progress.getLearnerId(), progress.getStatus(), progress.getProgressPercent(),
                progress.getLastPositionSeconds(), progress.getTimeSpentSeconds(), progress.getStartedAt(),
                progress.getCompletedAt(), progress.getLastAccessedAt());
    }
}

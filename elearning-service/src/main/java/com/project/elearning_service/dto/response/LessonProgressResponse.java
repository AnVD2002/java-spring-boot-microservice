package com.project.elearning_service.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LessonProgressResponse(
        UUID id,
        UUID enrollmentId,
        UUID lessonId,
        UUID learnerId,
        Integer status,
        BigDecimal progressPercent,
        Integer lastPositionSeconds,
        Integer timeSpentSeconds,
        Instant startedAt,
        Instant completedAt,
        Instant lastAccessedAt
) {
}

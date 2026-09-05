package com.project.e_learning_service.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CourseEnrollmentResponse(
        UUID id,
        UUID courseId,
        UUID learnerId,
        Integer status,
        BigDecimal progressPercent,
        Instant enrolledAt,
        Instant startedAt,
        Instant completedAt
) {
}

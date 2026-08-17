package com.project.elearning_service.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CourseResponse(
        UUID id,
        String code,
        String title,
        String shortDescription,
        String description,
        String thumbnailUrl,
        String level,
        String language,
        Integer status,
        UUID instructorId,
        Instant publishedAt
) {
}

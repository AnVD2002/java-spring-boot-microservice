package com.project.elearning_service.dto.response;

import java.util.UUID;

public record LessonResponse(
        UUID id,
        UUID courseId,
        UUID moduleId,
        String title,
        String summary,
        String lessonType,
        String contentUrl,
        Integer orderIndex,
        Integer durationMinutes,
        Boolean isPreview,
        Integer status
) {
}

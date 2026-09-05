package com.project.e_learning_service.dto.response;

import java.util.UUID;

public record LessonDocumentResponse(
        UUID id,
        UUID lessonId,
        UUID documentId,
        Integer documentVersion,
        String title,
        String documentType,
        Boolean isRequired,
        Integer displayOrder,
        Integer status
) {
}

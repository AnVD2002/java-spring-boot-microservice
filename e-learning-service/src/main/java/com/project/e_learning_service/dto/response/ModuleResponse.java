package com.project.e_learning_service.dto.response;

import java.util.UUID;

public record ModuleResponse(
        UUID id,
        UUID courseId,
        String title,
        String description,
        Integer orderIndex,
        Integer status
) {
}

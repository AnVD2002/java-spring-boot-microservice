package com.project.documents_service.dto.response;

import java.util.UUID;

public record DocumentAccessResponse(
        UUID documentId,
        String url,
        Integer expiresInSeconds
) {
}

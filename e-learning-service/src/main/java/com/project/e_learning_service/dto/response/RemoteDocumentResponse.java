package com.project.e_learning_service.dto.response;

import java.util.UUID;

public record RemoteDocumentResponse(
        UUID id,
        String title,
        String description,
        String fileUrl,
        String fileType,
        String storageProvider,
        String storageKey,
        String originalFileName,
        String mimeType,
        Long fileSize,
        String checksum,
        Integer version,
        Integer status,
        UUID ownerId
) {
}

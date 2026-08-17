package com.project.documents_service.storage;

public record StoredObject(
        String provider,
        String storageKey,
        String fileUrl,
        String checksum
) {
}

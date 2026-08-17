package com.project.documents_service.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageProvider {
    StoredObject store(MultipartFile file, String folder);

    String getPresignedUrl(String storageKey);
}

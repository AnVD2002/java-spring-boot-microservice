package com.project.documents_service.service;

import com.project.documents_service.dto.request.DocumentUploadRequest;
import com.project.documents_service.dto.response.DocumentAccessResponse;
import com.project.documents_service.dto.response.DocumentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface DocumentService {
    DocumentResponse upload(DocumentUploadRequest request, MultipartFile file, UUID ownerId);

    DocumentResponse getById(UUID id);

    DocumentAccessResponse getAccessUrl(UUID id);
}

package com.project.documents_service.service.impl;

import com.project.documents_service.config.MinioProperties;
import com.project.documents_service.dto.request.DocumentUploadRequest;
import com.project.documents_service.dto.response.DocumentAccessResponse;
import com.project.documents_service.dto.response.DocumentResponse;
import com.project.documents_service.entity.Document;
import com.project.documents_service.repository.DocumentRepository;
import com.project.documents_service.service.DocumentService;
import com.project.documents_service.storage.StoredObject;
import com.project.documents_service.storage.StorageProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final StorageProvider storageProvider;
    private final MinioProperties minioProperties;

    @Override
    public DocumentResponse upload(DocumentUploadRequest request, MultipartFile file, UUID ownerId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        StoredObject storedObject = storageProvider.store(file, "learning");
        Document document = Document.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .fileUrl(storedObject.fileUrl())
                .fileType(request.getDocumentType())
                .storageProvider(storedObject.provider())
                .storageKey(storedObject.storageKey())
                .originalFileName(file.getOriginalFilename())
                .mimeType(file.getContentType())
                .fileSize(file.getSize())
                .checksum(storedObject.checksum())
                .version(1)
                .status(request.getStatus() == null ? 1 : request.getStatus())
                .ownerId(ownerId)
                .build();

        return toResponse(documentRepository.save(document));
    }

    @Override
    public DocumentResponse getById(UUID id) {
        return toResponse(findDocument(id));
    }

    @Override
    public DocumentAccessResponse getAccessUrl(UUID id) {
        Document document = findDocument(id);
        String url = storageProvider.getPresignedUrl(document.getStorageKey());
        return new DocumentAccessResponse(document.getId(), url, minioProperties.getPresignedUrlExpirySeconds());
    }

    private Document findDocument(UUID id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + id));
    }

    private DocumentResponse toResponse(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getTitle(),
                document.getDescription(),
                document.getFileUrl(),
                document.getFileType(),
                document.getStorageProvider(),
                document.getStorageKey(),
                document.getOriginalFileName(),
                document.getMimeType(),
                document.getFileSize(),
                document.getChecksum(),
                document.getVersion(),
                document.getStatus(),
                document.getOwnerId()
        );
    }
}

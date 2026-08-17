package com.project.documents_service.controller;

import com.project.common_lib_service.dto.ResponseData;
import com.project.common_lib_service.dto.CustomUserPrincipal;
import com.project.common_lib_service.utils.ResponseUtils;
import com.project.documents_service.dto.request.DocumentUploadRequest;
import com.project.documents_service.dto.response.DocumentAccessResponse;
import com.project.documents_service.dto.response.DocumentResponse;
import com.project.documents_service.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/health")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<ResponseData<String>> health() {
        return ResponseUtils.success("documents-service is running");
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER')")
    public ResponseEntity<ResponseData<DocumentResponse>> upload(
            @Valid @ModelAttribute DocumentUploadRequest request,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseUtils.success(documentService.upload(request, file, principal.getUserId()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER', 'USER')")
    public ResponseEntity<ResponseData<DocumentResponse>> getById(@PathVariable UUID id) {
        return ResponseUtils.success(documentService.getById(id));
    }

    @GetMapping("/{id}/access-url")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'CONTENT_MANAGER', 'USER')")
    public ResponseEntity<ResponseData<DocumentAccessResponse>> getAccessUrl(@PathVariable UUID id) {
        return ResponseUtils.success(documentService.getAccessUrl(id));
    }
}

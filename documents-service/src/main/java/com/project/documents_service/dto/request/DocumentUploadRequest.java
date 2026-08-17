package com.project.documents_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentUploadRequest {
    @NotBlank
    private String title;

    private String description;
    private String documentType;
    private Integer status;
}

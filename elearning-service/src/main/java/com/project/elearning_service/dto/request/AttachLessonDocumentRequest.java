package com.project.elearning_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AttachLessonDocumentRequest {
    @NotNull
    private UUID documentId;

    private Integer documentVersion;
    private String title;
    private String documentType;
    private Boolean isRequired;
    private Integer displayOrder;
    private Integer status;
}

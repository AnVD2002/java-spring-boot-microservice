package com.project.e_learning_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadLessonDocumentRequest {
    @NotBlank
    private String title;

    private String description;
    private String documentType;
    private Boolean isRequired;
    private Integer displayOrder;
    private Integer status;
}

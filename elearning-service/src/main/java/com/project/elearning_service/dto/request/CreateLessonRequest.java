package com.project.elearning_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateLessonRequest {
    private UUID moduleId;

    @NotBlank
    private String title;

    private String summary;
    private String lessonType;
    private String contentUrl;
    private Integer orderIndex;
    private Integer durationMinutes;
    private Boolean isPreview;
    private Integer status;
}

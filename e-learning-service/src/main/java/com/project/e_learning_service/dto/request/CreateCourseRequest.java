package com.project.e_learning_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCourseRequest {
    private String code;

    @NotBlank
    private String title;

    private String shortDescription;
    private String description;
    private String thumbnailUrl;
    private String level;
    private String language;
    private Integer status;
}

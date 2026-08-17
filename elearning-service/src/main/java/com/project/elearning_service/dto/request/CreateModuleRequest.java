package com.project.elearning_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateModuleRequest {
    @NotBlank
    private String title;

    private String description;
    private Integer orderIndex;
    private Integer status;
}

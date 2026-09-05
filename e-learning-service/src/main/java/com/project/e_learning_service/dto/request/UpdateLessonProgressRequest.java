package com.project.e_learning_service.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class UpdateLessonProgressRequest {
    @NotNull
    private UUID lessonId;

    @DecimalMin("0.00")
    @DecimalMax("100.00")
    private BigDecimal progressPercent;

    private Integer lastPositionSeconds;
    private Integer timeSpentSeconds;
    private Integer status;
}

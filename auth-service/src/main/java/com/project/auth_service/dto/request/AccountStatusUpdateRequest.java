package com.project.auth_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountStatusUpdateRequest {
    @NotNull
    private Integer status;
}

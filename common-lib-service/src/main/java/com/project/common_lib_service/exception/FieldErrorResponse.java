package com.project.common_lib_service.exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FieldErrorResponse {
    private String errorCode;
    private String message;
    private String field;
}

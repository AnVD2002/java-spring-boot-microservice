package com.project.common_lib_service.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse implements AbstractError {
    private int code;

    private String errorCode;

    private String message;

    private HttpStatus httpStatus;
    private String extras;
    private String url;
}

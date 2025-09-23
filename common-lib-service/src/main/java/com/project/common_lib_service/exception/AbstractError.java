package com.project.common_lib_service.exception;

import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public interface AbstractError {
    String getErrorCode();

    int getCode();

    HttpStatus getHttpStatus();

}

package com.project.common_lib_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthenticationError implements AbstractError{
    ERROR_001(401, "ERROR-001", HttpStatus.UNAUTHORIZED),
    ERROR_002(401, "ERROR-002", HttpStatus.UNAUTHORIZED),
    ERROR_003(401, "ERROR-003", HttpStatus.UNAUTHORIZED),
    ERROR_004(401, "ERROR-004", HttpStatus.UNAUTHORIZED);


    private final int code;
    private final String errorCode;
    private final HttpStatus httpStatus;


    AuthenticationError(int code, String errorCode, HttpStatus httpStatus) {
        this.code = code;
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

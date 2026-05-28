package com.project.common_lib_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthenticationError implements AbstractError{
    AUTH_001(401, "AUTH-001", HttpStatus.UNAUTHORIZED),
    AUTH_002(401, "AUTH-002", HttpStatus.UNAUTHORIZED),
    AUTH_003(401, "AUTH-003", HttpStatus.UNAUTHORIZED),
    AUTH_004(429, "AUTH-004", HttpStatus.TOO_MANY_REQUESTS),
    AUTH_005(400, "AUTH-005", HttpStatus.BAD_REQUEST),  // Invalid or expired OTP
    AUTH_006(400, "AUTH-006", HttpStatus.BAD_REQUEST);  // Invalid or expired reset token


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

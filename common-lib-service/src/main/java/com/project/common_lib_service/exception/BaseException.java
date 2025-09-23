package com.project.common_lib_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {
    @Getter
    private int code;
    @Getter
    private String errorCode;
    private final String message;
    @Getter
    private HttpStatus httpStatus;
    @Getter
    private AbstractError abstractError;
    @Getter
    private String extras;

    protected BaseException(AbstractError abstractError, String message, String extras) {
        super(abstractError.getErrorCode(), null);
        this.code = abstractError.getCode();
        this.errorCode = abstractError.getErrorCode();
        this.httpStatus = abstractError.getHttpStatus();
        this.abstractError = abstractError;
        this.message = message;
        this.extras = extras;
    }

    protected BaseException(AbstractError abstractError, String message) {
        super(abstractError.getErrorCode(), null);
        this.code = abstractError.getCode();
        this.errorCode = abstractError.getErrorCode();
        this.httpStatus = abstractError.getHttpStatus();
        this.abstractError = abstractError;
        this.message = message;
    }

    protected BaseException(AbstractError abstractError) {
        super(abstractError.getErrorCode(), null);
        this.code = abstractError.getCode();
        this.errorCode = abstractError.getErrorCode();
        this.httpStatus = abstractError.getHttpStatus();
        this.abstractError = abstractError;
        this.message = abstractError.getErrorCode();
    }

    @Override
    public String getMessage() {
        return message;
    }

}

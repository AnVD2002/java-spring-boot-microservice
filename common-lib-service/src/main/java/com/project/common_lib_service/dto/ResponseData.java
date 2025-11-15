package com.project.common_lib_service.dto;


import com.project.common_lib_service.exception.FieldErrorResponse;
import lombok.Data;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

@Data
public class ResponseData<T> implements Serializable {
    private int code;
    private String errorCode;
    private String message;
    private HttpStatus httpStatus;
    private List<FieldErrorResponse> fieldErrors;

    @Setter
    private T data;

    public ResponseData<T> success(T data) {
        this.code = 200;
        this.message = "success";
        this.data = data;
        this.httpStatus = HttpStatus.OK;
        return this;
    }

    public ResponseData<T> error(int code, String errorCode ,String message, T data) {
        this.code = code;
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
        return this;
    }

    public ResponseData<T> error(int code, String errorCode, String message) {
        this.code = code;
        this.errorCode = errorCode;
        this.message = message;
        return this;
    }

    public ResponseData<T> error(int code, String errorCode, String message, HttpStatus httpStatus) {
        this.code = code;
        this.errorCode = errorCode;
        this.message = message;
        this.httpStatus = httpStatus;
        return this;
    }

    public ResponseData<T> error(int code, List<FieldErrorResponse> fieldErrors , HttpStatus httpStatus) {
        this.code = code;
        this.fieldErrors = fieldErrors;
        this.httpStatus = httpStatus;
        return this;
    }

    public ResponseData<T> success() {
        this.code = 200;
        this.message = "success";
        this.httpStatus = HttpStatus.OK;
        return this;
    }
}

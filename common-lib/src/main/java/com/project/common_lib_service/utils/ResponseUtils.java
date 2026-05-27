package com.project.common_lib_service.utils;

import com.project.common_lib_service.dto.ResponseData;
import com.project.common_lib_service.exception.FieldErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ResponseUtils {
    public static <T> ResponseEntity<ResponseData<T>> success() {
        return ResponseEntity.ok(new ResponseData<T>().success());}

    public static <T> ResponseEntity<ResponseData<T>> success(T o) {
        return ResponseEntity.ok(new ResponseData<T>().success(o));}

    public static <T> ResponseEntity<ResponseData<T>> error(int code, List<FieldErrorResponse> fieldErrors, HttpStatus httpStatus ) {
        return ResponseEntity.status(httpStatus).body(getListResponseErrorResponse(code,fieldErrors,httpStatus));
    }

    public static <T> ResponseData<T> getListResponseErrorResponse(int code, List<FieldErrorResponse> fieldErrors, HttpStatus httpStatus ) {
        return new ResponseData<T>().error(code, fieldErrors, httpStatus);
    }
}

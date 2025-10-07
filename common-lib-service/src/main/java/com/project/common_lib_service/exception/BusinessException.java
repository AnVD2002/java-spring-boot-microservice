package com.project.common_lib_service.exception;
import lombok.Getter;

@Getter
public class BusinessException extends BaseException {

    public BusinessException(AbstractError abstractError, String message, String extras) {
        super(abstractError, message, extras);
    }

    public BusinessException(AbstractError abstractError, String message) {
        super(abstractError, message);
    }

    public BusinessException(AbstractError abstractError) {
        super(abstractError);
    }



}

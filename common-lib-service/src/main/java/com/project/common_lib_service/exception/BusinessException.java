package com.project.common_lib_service.exception;
import lombok.Getter;

@Getter
public class BusinessException extends BaseException {

    protected BusinessException(AbstractError abstractError, String message, String extras) {
        super(abstractError, message, extras);
    }

    protected BusinessException(AbstractError abstractError, String message) {
        super(abstractError, message);
    }

    protected BusinessException(AbstractError abstractError) {
        super(abstractError);
    }



}

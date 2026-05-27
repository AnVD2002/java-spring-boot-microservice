package com.project.common_lib_service.exception;

public class SystemException extends BaseException{
    public SystemException(AbstractError abstractError, String message, String extras) {
        super(abstractError, message, extras);
    }

    public SystemException(AbstractError abstractError, String message) {
        super(abstractError, message);
    }

    public SystemException(AbstractError abstractError) {
        super(abstractError);
    }
}

package com.project.common_lib_service.exception;

import lombok.Getter;

@Getter
public class AuthenticationException extends BaseException {
    public AuthenticationException(AbstractError abstractError, String message, String extras) {
        super(abstractError, message, extras);
    }

    public AuthenticationException(AbstractError abstractError, String message) {
        super(abstractError, message);
    }

    public AuthenticationException(AbstractError abstractError) {
        super(abstractError);
    }
}

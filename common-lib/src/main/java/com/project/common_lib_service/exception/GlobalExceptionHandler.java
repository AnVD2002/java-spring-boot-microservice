package com.project.common_lib_service.exception;

import com.project.common_lib_service.config.MessageResource;
import com.project.common_lib_service.dto.ResponseData;
import com.project.common_lib_service.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageResource messageResource;

    /**
     * Handle all BaseException subclasses: BusinessException, AuthenticationException, SystemException
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(
            BaseException ex,
            HttpServletRequest request) {

        log.warn("BaseException [{}] at [{}]: {}", ex.getErrorCode(), request.getRequestURI(), ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .extras(ex.getExtras())
                .httpStatus(ex.getHttpStatus())
                .url(request.getRequestURI())
                .build();

        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    /**
     * Handle ResponseStatusException
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

        ErrorResponse error = ErrorResponse.builder()
                .code(status.value())
                .message(ex.getReason())
                .httpStatus(status)
                .url(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    /**
     * Handle MethodArgumentNotValidException (validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseData<List<FieldErrorResponse>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {

        List<FieldErrorResponse> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> {
                    String errorCode = fieldError.getDefaultMessage();
                    String rawMessage = messageResource.getMessage(errorCode);

                    Integer min = extractArgument(fieldError.getArguments(), 2);
                    Integer max = extractArgument(fieldError.getArguments(), 1);

                    String resolvedMessage = rawMessage
                            .replace("{min}", min != null ? min.toString() : "")
                            .replace("{max}", max != null ? max.toString() : "");

                    return FieldErrorResponse.builder()
                            .field(fieldError.getField())
                            .errorCode(errorCode)
                            .message(resolvedMessage)
                            .build();
                })
                .toList();

        return ResponseUtils.error(
                HttpStatus.BAD_REQUEST.value(),
                fieldErrors,
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Handle generic Exception — last resort
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        String path = request.getRequestURI();
        log.error("Unhandled exception at [{}]", path, ex);

        ErrorResponse error = ErrorResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Something went wrong. Please contact support.")
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .errorCode("ERROR-500")
                .url(path)
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private Integer extractArgument(Object[] arguments, int index) {
        if (arguments != null && arguments.length > index && arguments[index] instanceof Integer value) {
            return value;
        }
        return null;
    }
}

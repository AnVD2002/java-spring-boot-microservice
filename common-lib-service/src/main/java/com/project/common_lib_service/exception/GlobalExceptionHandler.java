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
     * handle BusinessException
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<AbstractError> handleBusinessException(BusinessException ex, HttpServletRequest request) {
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
     * Handle ResponseStatusException thrown by controllers or services.
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex,
                                                                       HttpServletRequest request) {

        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        ErrorResponse error = ErrorResponse.builder()
                .code(ex.getStatusCode().value())
                .message(ex.getReason())
                .httpStatus(status)
                .url(request.getRequestURI())
                .build();

        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    /**
     * Handle all other exceptions that are not explicitly handled.
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Something went wrong. Please contact support.")
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .errorCode("ERROR-500")
                .url(request.getRequestURI())
                .build();

        log.error("Unhandled exception at [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * handle MethodArgumentNotValidException
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseData<List<FieldErrorResponse>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<FieldErrorResponse> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    String errorCode = fieldError.getDefaultMessage();
                    String rawMessage = messageResource.getMessage(errorCode);

                    // Lấy min/max nếu có
                    Integer min = extractArgument(fieldError.getArguments(), 2);
                    Integer max = extractArgument(fieldError.getArguments(), 1);

                    // Resolve message
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
     * Helper method lấy argument an toàn
     */
    private Integer extractArgument(Object[] arguments, int index) {
        if (arguments != null && arguments.length > index && arguments[index] instanceof Integer value) {
            return value;
        }
        return null;
    }
}

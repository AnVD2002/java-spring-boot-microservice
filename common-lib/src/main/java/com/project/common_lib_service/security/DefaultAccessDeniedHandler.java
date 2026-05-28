package com.project.common_lib_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.common_lib_service.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class DefaultAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        ErrorResponse body = ErrorResponse.builder()
                .code(HttpStatus.FORBIDDEN.value())
                .errorCode("ERROR-008")
                .message("Permission denied")
                .httpStatus(HttpStatus.FORBIDDEN)
                .url(request.getRequestURI())
                .build();

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}

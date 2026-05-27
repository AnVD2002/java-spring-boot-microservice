package com.project.common_lib_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.common_lib_service.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public record DefaultAuthenticationEntryPoint(ObjectMapper objectMapper, int status,
                                              String message) implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        HttpStatus httpStatus = HttpStatus.resolve(status);
        if (httpStatus == null) httpStatus = HttpStatus.UNAUTHORIZED;

        ErrorResponse body = ErrorResponse.builder()
                .code(status)
                .message(message)
                .httpStatus(httpStatus)
                .url(request.getRequestURI())
                .build();

        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}

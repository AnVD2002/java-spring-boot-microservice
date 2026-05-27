package com.project.common_lib_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class RestTemplateErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError()
                || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        int statusCode = response.getStatusCode().value();
        String body = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
        log.error("RestTemplate error - {} {}: HTTP {} - {}", method, url, statusCode, body);
        throw new SystemException(SystemError.ERROR_500, "External service error: HTTP " + statusCode);
    }
}

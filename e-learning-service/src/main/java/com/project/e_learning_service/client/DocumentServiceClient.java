package com.project.e_learning_service.client;

import com.project.common_lib_service.dto.CustomUserPrincipal;
import com.project.common_lib_service.dto.ResponseData;
import com.project.common_lib_service.utils.SecurityHeaders;
import com.project.e_learning_service.dto.request.UploadLessonDocumentRequest;
import com.project.e_learning_service.dto.response.RemoteDocumentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentServiceClient {

    private static final String DOCUMENTS_SERVICE_URL = "http://documents-service/api/v1/documents";

    private final RestTemplate restTemplate;

    @Value("${internal.secret}")
    private String internalSecret;

    public RemoteDocumentResponse upload(MultipartFile file, UploadLessonDocumentRequest request, CustomUserPrincipal principal) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", toResource(file));
        body.add("title", request.getTitle());
        if (request.getDescription() != null) {
            body.add("description", request.getDescription());
        }
        if (request.getDocumentType() != null) {
            body.add("documentType", request.getDocumentType());
        }
        if (request.getStatus() != null) {
            body.add("status", request.getStatus());
        }

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, buildHeaders(principal));

        ResponseEntity<ResponseData<RemoteDocumentResponse>> response = restTemplate.exchange(
                DOCUMENTS_SERVICE_URL,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody().getData();
    }

    private HttpHeaders buildHeaders(CustomUserPrincipal principal) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set(SecurityHeaders.INTERNAL_SECRET, internalSecret);
        headers.set(SecurityHeaders.USER_ID, principal.getUserId().toString());
        headers.set(SecurityHeaders.USERNAME, principal.getUsername());
        headers.set(SecurityHeaders.ROLES, String.join(",", principal.getRoles() == null ? List.of() : principal.getRoles()));
        return headers;
    }

    private ByteArrayResource toResource(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            return new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read uploaded file", e);
        }
    }
}

//package com.project.common_lib_service.service;
//
//import com.project.common_lib_service.config.ServiceUrlProperties;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//@Service
//@RequiredArgsConstructor
//public class ServiceWebClient {
//    private final WebClient webClient;
//    private final ServiceUrlProperties serviceUrlProperties;
//
//    /**
//     * Makes a GET request to another service
//     */
//    public <T> Mono<T> get(String url, Class<T> responseType) {
//        return webClient.get()
//                .uri(url)
//                .retrieve()
//                .bodyToMono(responseType);
//    }
//
//    /**
//     * Makes a GET request with auth header
//     */
//    public <T> Mono<T> getWithAuth(String url, String token, Class<T> responseType) {
//        return webClient.get()
//                .uri(url)
//                .headers(headers -> applyAuthHeaders(headers, token))
//                .retrieve()
//                .bodyToMono(responseType);
//    }
//
//    /**
//     * Makes a POST request to another service
//     */
//    public <T> Mono<T> post(String url, Object request, Class<T> responseType) {
//        return webClient.post()
//                .uri(url)
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(request)
//                .retrieve()
//                .bodyToMono(responseType);
//    }
//
//    /**
//     * Makes a POST request with auth header
//     */
//    public <T> Mono<T> postWithAuth(String url, Object request, String token, Class<T> responseType) {
//        return webClient.post()
//                .uri(url)
//                .headers(headers -> applyAuthHeaders(headers, token))
//                .bodyValue(request)
//                .retrieve()
//                .bodyToMono(responseType);
//    }
//
//    /**
//     * Makes a PUT request with auth header
//     */
//    public <T> Mono<T> putWithAuth(String url, Object request, String token, Class<T> responseType) {
//        return webClient.put()
//                .uri(url)
//                .headers(headers -> applyAuthHeaders(headers, token))
//                .bodyValue(request)
//                .retrieve()
//                .bodyToMono(responseType);
//    }
//
//    /**
//     * Makes a DELETE request with auth header
//     */
//    public <T> Mono<T> deleteWithAuth(String url, String token, Class<T> responseType) {
//        return webClient.delete()
//                .uri(url)
//                .headers(headers -> applyAuthHeaders(headers, token))
//                .retrieve()
//                .bodyToMono(responseType);
//    }
//
//    /**
//     * Makes a generic exchange call
//     */
//    public <T> Mono<T> exchange(String url, HttpMethod method, Object body, String token, Class<T> responseType) {
//        return webClient.method(method)
//                .uri(url)
//                .headers(headers -> {
//                    if (token != null && !token.isEmpty()) {
//                        applyAuthHeaders(headers, token);
//                    }
//                })
//                .bodyValue(body != null ? body : "")
//                .retrieve()
//                .bodyToMono(responseType);
//    }
//
//    /**
//     * Service-to-service call with automatic service discovery
//     * Format: service-name/endpoint
//     */
//    public <T> Mono<T> callService(String serviceName, String endpoint, HttpMethod method,
//                                   Object body, String token, Class<T> responseType) {
//        String baseUrl = resolveServiceUrl(serviceName);
//        String fullUrl = baseUrl + "/" + endpoint;
//
//        WebClient.RequestBodySpec request = webClient.method(method)
//                .uri(fullUrl)
//                .headers(headers -> applyAuthHeaders(headers, token));
//
//        if (body != null) {
//            request.bodyValue(body);
//        }
//
//        return request.retrieve()
//                .bodyToMono(responseType);
//    }
//
//    private void applyAuthHeaders(HttpHeaders headers, String token) {
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
//        if (token != null && !token.isEmpty()) {
//            headers.setBearerAuth(token);
//        }
//    }
//
//    private String resolveServiceUrl(String serviceName) {
//        return serviceUrlProperties.getServiceUrl(serviceName);
//    }
//}

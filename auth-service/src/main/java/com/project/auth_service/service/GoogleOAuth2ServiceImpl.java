package com.project.auth_service.service;

import com.project.auth_service.dto.response.GoogleUserInfo;
import com.project.auth_service.utils.LoginType;
import com.project.common_lib_service.exception.SystemError;
import com.project.common_lib_service.exception.SystemException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.adapter.DefaultServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class GoogleOAuth2ServiceImpl implements GoogleOAuth2Service {
    private final WebClient googleClient;

    private final OAuth2AuthorizationRequestResolver authorizationRequestResolver;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
    private String userInfoUri;

    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final Random RANDOM = new Random();

    private static final String TOPIC = "email-code-topic";

    @Override
    public String authenticateAndFetchProfile(String loginType, String code) {
        try {

            LoginType type = LoginType.valueOf(loginType.toUpperCase());

            switch (type) {
                case GOOGLE:
                    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
                    body.add("code", code);
                    body.add("client_id", clientId);
                    body.add("client_secret", clientSecret);
                    body.add("redirect_uri", redirectUri);
                    body.add("grant_type", "authorization_code");

                    Map<String, Object> tokenResponse = googleClient.post()
                            .uri("/token")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .bodyValue(body)
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                            })
                            .block();

                    String email = "";

                    if (tokenResponse != null && tokenResponse.containsKey("email")) {
                        email = (String) tokenResponse.get("email");
                    }

                    if (email == null || email.isBlank()) {
                        throw new SystemException(SystemError.ERROR_030);
                    }

                    return Optional.of(tokenResponse)
                            .map(res -> res.get("access_token"))
                            .map(Object::toString)
                            .orElseThrow(() -> new SystemException(SystemError.ERROR_029));

                case FACEBOOK:
                    // TODO: implement facebook login

                default:
                    throw new SystemException(SystemError.ERROR_029, "Unsupported login type: " + loginType);
            }
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw new SystemException(SystemError.ERROR_500, e.getMessage());
        }
    }

//    public static String generateCode() {
//        StringBuilder sb = new StringBuilder(6);
//        for (int i = 0; i < 6; i++) {
//            int index = RANDOM.nextInt(CHAR_POOL.length());
//            sb.append(CHAR_POOL.charAt(index));
//        }
//        return sb.toString();
//    }

    @Override
    public String generateUrl(HttpServletRequest request, String registrationId) {
        OAuth2AuthorizationRequest authorizationRequest =
                authorizationRequestResolver.resolve(request, registrationId);

        if (authorizationRequest == null) {
            throw new SystemException(SystemError.ERROR_500, "Cannot resolve authorization request for " + registrationId);
        }

        return authorizationRequest.getAuthorizationRequestUri();
    }

    public GoogleUserInfo getGoogleUserInfo(String accessToken) {
        return googleClient.get()
                .uri(userInfoUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(GoogleUserInfo.class)
                .block();
    }


}

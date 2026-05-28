package com.project.auth_service.service.provider.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.project.auth_service.dto.response.GoogleUserInfo;
import com.project.auth_service.service.provider.GoogleOAuth2Service;
import com.project.auth_service.utils.LoginType;
import com.project.common_lib_service.exception.SystemError;
import com.project.common_lib_service.exception.SystemException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class GoogleOAuth2ServiceImpl implements GoogleOAuth2Service {
    private final WebClient googleClient;

    @Value("${google.oauth.client-id}")
    private String clientId;

    @Value("${google.oauth.client-secret}")
    private String clientSecret;

    @Value("${google.oauth.redirect-uri}")
    private String redirectUri;

    @Value("${google.oauth.user-info-uri}")
    private String userInfoUri;

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

                    if (!CollectionUtils.isEmpty(tokenResponse)) {
                        return Optional.of(tokenResponse)
                                .map(res -> res.get("id_token"))
                                .map(Object::toString)
                                .orElseThrow(() -> new SystemException(SystemError.ERROR_029));
                    }
                    throw new SystemException(SystemError.ERROR_029, "Empty token response from Google");

                case FACEBOOK:
                    // TODO: implement facebook login
                    throw new SystemException(SystemError.ERROR_029, "Facebook login not yet supported");

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
    public String generateUrl() {
        return UriComponentsBuilder.fromUriString("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "openid email profile")
                .queryParam("access_type", "offline")
                .build()
                .toUriString();
    }

    public GoogleUserInfo getGoogleUserInfo(String accessToken) {
        return googleClient.get()
                .uri(userInfoUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(GoogleUserInfo.class)
                .block();
    }

    public GoogleUserInfo verifyAndDecode(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            return getGoogleUserInfo(idToken);

        } catch (Exception e) {
            throw new RuntimeException("Failed to verify Google ID Token", e);
        }
    }

    private static GoogleUserInfo getGoogleUserInfo(GoogleIdToken idToken) {
        if (idToken == null) {
            throw new RuntimeException("Invalid Google ID Token");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();

        GoogleUserInfo dto = new GoogleUserInfo();
        dto.setSub(payload.getSubject());
        dto.setEmail(payload.getEmail());
        dto.setEmailVerified(payload.getEmailVerified());
        dto.setName((String) payload.get("name"));
        dto.setGivenName((String) payload.get("given_name"));
        dto.setFamilyName((String) payload.get("family_name"));
        dto.setPicture((String) payload.get("picture"));
        return dto;
    }
}

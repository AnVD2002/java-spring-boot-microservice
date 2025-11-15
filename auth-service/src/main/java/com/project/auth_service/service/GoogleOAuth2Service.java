package com.project.auth_service.service;

import com.project.auth_service.dto.response.GoogleUserInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

public interface GoogleOAuth2Service {
    String authenticateAndFetchProfile(String loginType, String code);

    public String generateUrl(HttpServletRequest request, String registrationId);

    GoogleUserInfo getGoogleUserInfo(String accessToken);
}

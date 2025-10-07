package com.project.auth_service.service;

import com.project.auth_service.dto.response.GoogleUserInfo;
import jakarta.servlet.http.HttpServletRequest;

public interface GoogleOAuth2Service {
    String authenticateAndFetchProfile(String loginType, String code);

    String generateUrl(HttpServletRequest request, String registrationId);

    GoogleUserInfo getGoogleUserInfo(String accessToken);
}

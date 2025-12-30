package com.project.auth_service.service;

import com.project.auth_service.dto.response.GoogleUserInfo;

public interface GoogleOAuth2Service {
    String authenticateAndFetchProfile(String loginType, String code);

    String generateUrl();

    GoogleUserInfo getGoogleUserInfo(String accessToken);
}

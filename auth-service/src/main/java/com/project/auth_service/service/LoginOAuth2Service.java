package com.project.auth_service.service;

import com.project.auth_service.dto.request.LoginGoogleRequest;
import com.project.auth_service.dto.response.LoginResponse;

public interface LoginOAuth2Service {
    LoginResponse loginOauth2(LoginGoogleRequest loginGoogleRequest);
}

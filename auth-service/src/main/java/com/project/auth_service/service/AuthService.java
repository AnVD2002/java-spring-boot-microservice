package com.project.auth_service.service;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface AuthService {
    String authenticateAndFetchProfile(String loginType, String code);

    String generateUrl(HttpServletRequest request, String registrationId);
}

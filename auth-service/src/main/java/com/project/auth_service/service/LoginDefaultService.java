package com.project.auth_service.service;

import com.project.auth_service.dto.request.LoginDefaultRequest;
import com.project.auth_service.dto.response.LoginResponse;

import java.util.List;

public interface LoginDefaultService {
    LoginResponse loginDefault(LoginDefaultRequest loginDefaultRequest, String deviceIdRequest, String accountIdRequest);

    List<String> getListRoleByUsername(String username );

    List<String> getListRoleByEmail(String email);
}

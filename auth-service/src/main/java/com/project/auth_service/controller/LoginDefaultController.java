package com.project.auth_service.controller;

import com.project.auth_service.dto.request.LoginDefaultRequest;
import com.project.auth_service.dto.response.LoginResponse;
import com.project.auth_service.service.LoginDefaultService;
import com.project.common_lib_service.dto.ResponseData;
import com.project.common_lib_service.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/login/default")
@RequiredArgsConstructor
public class LoginDefaultController {

    public final LoginDefaultService loginDefaultService;

    @PostMapping
    public ResponseEntity<ResponseData<LoginResponse>> loginDefault(LoginDefaultRequest loginDefaultRequest, HttpServletRequest httpServletRequest) {

        String deviceId = httpServletRequest.getHeader("X-Device-ID");
        String accountId = httpServletRequest.getHeader("X-Account-ID");

        LoginResponse loginDefaultResponse = loginDefaultService.loginDefault(loginDefaultRequest, deviceId, accountId);

        return ResponseUtils.success(loginDefaultResponse);
    }
}

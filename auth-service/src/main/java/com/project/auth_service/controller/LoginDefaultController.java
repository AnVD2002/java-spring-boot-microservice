package com.project.auth_service.controller;

import com.project.auth_service.dto.request.LoginDefaultRequest;
import com.project.auth_service.dto.request.LoginNormalRequest;
import com.project.auth_service.dto.response.LoginResponse;
import com.project.auth_service.service.auth.LoginDefaultService;
import com.project.common_lib_service.dto.ResponseData;
import com.project.common_lib_service.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/login")
@RequiredArgsConstructor
public class LoginDefaultController {

    private final LoginDefaultService loginDefaultService;

    @PostMapping("/default")
    public ResponseEntity<ResponseData<LoginResponse>> loginDefault(@Valid @RequestBody LoginDefaultRequest loginDefaultRequest, HttpServletRequest httpServletRequest) {

        String deviceId = httpServletRequest.getHeader("X-Device-ID");

        LoginResponse loginDefaultResponse = loginDefaultService.loginDefault(loginDefaultRequest, deviceId);

        return ResponseUtils.success(loginDefaultResponse);
    }
}

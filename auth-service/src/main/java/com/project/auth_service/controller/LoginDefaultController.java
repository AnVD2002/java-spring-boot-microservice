package com.project.auth_service.controller;

import com.project.auth_service.dto.request.LoginDefaultRequest;
import com.project.auth_service.dto.response.LoginResponse;
import com.project.auth_service.service.auth.LoginDefaultService;
import com.project.common_lib_service.dto.ResponseData;
import com.project.common_lib_service.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/login")
@RequiredArgsConstructor
@Tag(name = "Auth Login", description = "Username and password login APIs")
public class LoginDefaultController {

    private static final String DEVICE_ID_HEADER = "X-Device-ID";

    private final LoginDefaultService loginDefaultService;

    @PostMapping("/default")
    @Operation(summary = "Login with username and password")
    public ResponseEntity<ResponseData<LoginResponse>> loginDefault(
            @Valid @RequestBody LoginDefaultRequest request,
            HttpServletRequest httpServletRequest) {
        String deviceId = httpServletRequest.getHeader(DEVICE_ID_HEADER);
        return ResponseUtils.success(loginDefaultService.loginDefault(request, deviceId));
    }
}

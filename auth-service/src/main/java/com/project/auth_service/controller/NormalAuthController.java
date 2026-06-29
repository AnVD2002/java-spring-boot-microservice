package com.project.auth_service.controller;

import com.project.auth_service.dto.request.AccountRegistrationRequestNormal;
import com.project.auth_service.dto.request.ConfirmRegistrationRequest;
import com.project.auth_service.service.facade.RegisterAccountFacade;
import com.project.common_lib_service.dto.ResponseData;
import com.project.common_lib_service.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/normal")
@RequiredArgsConstructor
@Tag(name = "Normal Auth", description = "Email/password registration APIs")
public class NormalAuthController {

    private final RegisterAccountFacade registerAccountFacade;

    @PostMapping("/register")
    @Operation(summary = "Register with email and password")
    public ResponseEntity<ResponseData<Void>> registerNormal(@Valid @RequestBody AccountRegistrationRequestNormal request) {
        registerAccountFacade.registerNormal(request);
        return ResponseUtils.success();
    }

    @PostMapping("/confirm")
    @Operation(summary = "Confirm email registration OTP")
    public ResponseEntity<ResponseData<Void>> confirmNormalRegistration(@Valid @RequestBody ConfirmRegistrationRequest request) {
        registerAccountFacade.confirmNormalRegistration(request);
        return ResponseUtils.success();
    }
}

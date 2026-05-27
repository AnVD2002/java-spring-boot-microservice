package com.project.auth_service.controller;

import com.project.auth_service.dto.request.AccountRegistrationRequestNormal;
import com.project.auth_service.service.facade.RegisterAccountFacade;
import com.project.common_lib_service.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/normal")
@RequiredArgsConstructor
public class NormalAuthController {
    private final RegisterAccountFacade registerAccountFacade;

    @PostMapping("/register")
    public ResponseEntity<?> registerWithGoogle(@RequestBody AccountRegistrationRequestNormal request) {
        registerAccountFacade.registerNormal(request);
        return ResponseUtils.success();
    }

}

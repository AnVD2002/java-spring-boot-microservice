package com.project.user_service.controller;

import com.project.common_lib_service.dto.ResponseData;
import com.project.common_lib_service.utils.ResponseUtils;
import com.project.user_service.dto.response.UserResponse;
import com.project.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Page<UserResponse>>> getAllUsers(
            @PageableDefault(size = 10, sort = "insertedAt") Pageable pageable) {
        return ResponseUtils.success(userService.getAllUsers(pageable));
    }
}

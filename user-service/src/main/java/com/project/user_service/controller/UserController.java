package com.project.user_service.controller;

import com.project.common_lib_service.dto.ResponseData;
import com.project.common_lib_service.dto.CustomUserPrincipal;
import com.project.common_lib_service.utils.ResponseUtils;
import com.project.user_service.dto.request.UserStatusUpdateRequest;
import com.project.user_service.dto.request.UserUpdateRequest;
import com.project.user_service.dto.response.UserResponse;
import com.project.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Admin", description = "User profile management APIs")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<ResponseData<UserResponse>> getMyProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        return ResponseUtils.success(userService.getMyProfile(principal.getUserId()));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<ResponseData<UserResponse>> update0eMyProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseUtils.success(userService.updateMyProfile(principal.getUserId(), request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPPORT','SUPER_ADMIN')")
    @Operation(summary = "Search users", description = "Search and filter users for the dashboard")
    public ResponseEntity<ResponseData<Page<UserResponse>>> getAllUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @PageableDefault(size = 10, sort = "insertedAt") Pageable pageable) {
        return ResponseUtils.success(userService.getAllUsers(keyword, status, pageable));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPPORT')")
    @Operation(summary = "Get user detail")
    public ResponseEntity<ResponseData<UserResponse>> getUser(@PathVariable UUID userId) {
        return ResponseUtils.success(userService.getUser(userId));
    }

    @GetMapping("/by-account/{accountId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPPORT')")
    @Operation(summary = "Get user by auth account id")
    public ResponseEntity<ResponseData<UserResponse>> getUserByAccountId(@PathVariable UUID accountId) {
        return ResponseUtils.success(userService.getUserByAccountId(accountId));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPPORT')")
    @Operation(summary = "Update user profile")
    public ResponseEntity<ResponseData<UserResponse>> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseUtils.success(userService.updateUser(userId, request));
    }

    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user profile status")
    public ResponseEntity<ResponseData<UserResponse>> updateUserStatus(
            @PathVariable UUID userId,
            @Valid @RequestBody UserStatusUpdateRequest request) {
        return ResponseUtils.success(userService.updateUserStatus(userId, request));
    }
}

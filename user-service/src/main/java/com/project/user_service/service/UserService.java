package com.project.user_service.service;

import com.project.user_service.dto.response.UserResponse;
import com.project.user_service.dto.request.UserStatusUpdateRequest;
import com.project.user_service.dto.request.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    Page<UserResponse> getAllUsers(String keyword, Integer status, Pageable pageable);

    UserResponse getUser(UUID userId);

    UserResponse getUserByAccountId(UUID accountId);

    UserResponse getMyProfile(UUID accountId);

    UserResponse updateMyProfile(UUID accountId, UserUpdateRequest request);

    UserResponse updateUser(UUID userId, UserUpdateRequest request);

    UserResponse updateUserStatus(UUID userId, UserStatusUpdateRequest request);
}

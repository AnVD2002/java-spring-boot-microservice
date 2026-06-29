package com.project.user_service.service.impl;

import com.project.user_service.dto.AccountCreatedEvent;
import com.project.user_service.dto.request.UserStatusUpdateRequest;
import com.project.user_service.dto.request.UserUpdateRequest;
import com.project.user_service.dto.response.UserResponse;
import com.project.user_service.entity.User;
import com.project.user_service.cache.UserAdminCache;
import com.project.user_service.mapper.UserMapper;
import com.project.user_service.repository.UserRepository;
import com.project.user_service.service.UserService;
import com.project.common_lib_service.service.AuditLogService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.project.user_service.enums.AuditAction.CREATE;
import static com.project.user_service.enums.AuditAction.UPDATE;
import static com.project.user_service.enums.AuditEntityType.USER;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserAdminCache userAdminCache;
    private final AuditLogService auditLogService;

    @Override
    public Page<UserResponse> getAllUsers(String keyword, Integer status, Pageable pageable) {
        return userRepository.searchForAdmin(normalizeLikeKeyword(keyword), status, pageable).map(userMapper::toResponse);
    }

    @Override
    public UserResponse getUser(UUID userId) {
        return userAdminCache.getUser(userId).orElseGet(() -> loadAndCacheUser(userId));
    }

    private UserResponse loadAndCacheUser(UUID userId) {
        UserResponse response = userMapper.toResponse(getUserOrThrow(userId));
        userAdminCache.putUser(userId, response);
        return response;
    }

    @Override
    public UserResponse getUserByAccountId(UUID accountId) {
        return userAdminCache.getUserByAccountId(accountId).orElseGet(() -> loadAndCacheUserByAccountId(accountId));
    }

    @Override
    public UserResponse getMyProfile(UUID accountId) {
        return getUserByAccountId(accountId);
    }

    @Override
    @Transactional
    public UserResponse updateMyProfile(UUID accountId, UserUpdateRequest request) {
        User user = getUserByAccountIdOrThrow(accountId);
        Map<String, Object> oldData = userSnapshot(user);
        updateProfileFields(user, request);
        User saved = userRepository.save(user);
        auditLogService.record(UPDATE, USER, saved.getId(), oldData, userSnapshot(saved));
        evictUserCaches(saved);
        return userMapper.toResponse(saved);
    }

    private UserResponse loadAndCacheUserByAccountId(UUID accountId) {
        User user = userRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NoSuchElementException("User not found for account: " + accountId));
        UserResponse response = userMapper.toResponse(user);
        userAdminCache.putUserByAccountId(accountId, response);
        return response;
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID userId, UserUpdateRequest request) {
        User user = getUserOrThrow(userId);
        Map<String, Object> oldData = userSnapshot(user);
        updateProfileFields(user, request);
        User saved = userRepository.save(user);
        auditLogService.record(UPDATE, USER, saved.getId(), oldData, userSnapshot(saved));
        evictUserCaches(saved);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(UUID userId, UserStatusUpdateRequest request) {
        User user = getUserOrThrow(userId);
        Map<String, Object> oldData = userSnapshot(user);
        user.setStatus(request.getStatus());
        User saved = userRepository.save(user);
        auditLogService.record(UPDATE, USER, saved.getId(), oldData, userSnapshot(saved));
        evictUserCaches(saved);
        return userMapper.toResponse(saved);
    }

    @Transactional
    public void createUser(AccountCreatedEvent event) {
        if (userRepository.existsByAccountId(event.getAccountId())) {
            return;
        }
        if (event.getEmail() == null) {
            throw new IllegalArgumentException("Email is null");
        }
        User user = User.builder()
                .accountId(event.getAccountId())
                .email(event.getEmail())
                .username(event.getUsername())
                .status(1)
                .build();
        User saved = userRepository.save(user);
        auditLogService.record(CREATE, USER, saved.getId(), null, userSnapshot(saved));
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
    }

    private User getUserByAccountIdOrThrow(UUID accountId) {
        return userRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NoSuchElementException("User not found for account: " + accountId));
    }

    private void updateProfileFields(User user, UserUpdateRequest request) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAddress(request.getAddress());
        user.setPhoneNumber(request.getPhoneNumber());
    }

    private void evictUserCaches(User user) {
        userAdminCache.evictUser(user.getId(), user.getAccountId());
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String normalizeLikeKeyword(String value) {
        return StringUtils.hasText(value) ? value.trim().toLowerCase(Locale.ROOT) : "";
    }

    private Map<String, Object> userSnapshot(User user) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", user.getId());
        data.put("accountId", user.getAccountId());
        data.put("email", user.getEmail());
        data.put("username", user.getUsername());
        data.put("firstName", user.getFirstName());
        data.put("lastName", user.getLastName());
        data.put("address", user.getAddress());
        data.put("phoneNumber", user.getPhoneNumber());
        data.put("status", user.getStatus());
        return data;
    }
}

package com.project.user_service.service.impl;

import com.project.user_service.dto.AccountCreatedEvent;
import com.project.user_service.dto.response.UserResponse;
import com.project.user_service.entity.User;
import com.project.user_service.repository.UserRepository;
import com.project.user_service.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toResponse);
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
        userRepository.save(user);
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .accountId(user.getAccountId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .insertedAt(user.getInsertedAt())
                .build();
    }
}

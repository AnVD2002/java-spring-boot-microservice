package com.project.user_service.mapper;

import com.project.user_service.dto.response.UserResponse;
import com.project.user_service.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
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

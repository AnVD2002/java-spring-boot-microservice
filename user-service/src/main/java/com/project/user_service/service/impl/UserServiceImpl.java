package com.project.user_service.service.impl;

import com.project.user_service.dto.AccountCreatedEvent;
import com.project.user_service.entity.User;
import com.project.user_service.repository.UserRepository;
import com.project.user_service.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    public void createUser(AccountCreatedEvent event) {

        // CASE 1: user đã tồn tại (idempotent)
        if (userRepository.existsByAccountId(event.getAccountId())) {
            return; // coi như success
        }

        // CASE 2: validate lỗi
        if (event.getEmail() == null) {
            throw new IllegalArgumentException("Email is null");
        }

        // CASE 3: save user
        User user = User.builder()
                .accountId(event.getAccountId())
                .email(event.getEmail())
                .build();

        userRepository.save(user);

        // nếu lỗi DB → throw exception → rollback user DB
    }
}

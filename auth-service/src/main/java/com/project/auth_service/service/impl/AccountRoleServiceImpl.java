package com.project.auth_service.service.impl;

import com.project.auth_service.repository.AccountRoleRepository;
import com.project.auth_service.service.AccountRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountRoleServiceImpl implements AccountRoleService {

    private final AccountRoleRepository accountRoleRepository;

    @Override
    public List<String> getRoleNameByEmail(String email) {
        return accountRoleRepository.getRoleNameByEmail(email);
    }

    @Override
    public List<String> getRoleNameByUsername(String username) {
        return accountRoleRepository.getRoleNameByUsername(username);
    }
}

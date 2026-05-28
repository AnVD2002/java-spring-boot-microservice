package com.project.auth_service.service.impl;

import com.project.auth_service.entity.Account;
import com.project.auth_service.entity.AccountRole;
import com.project.auth_service.entity.Role;
import com.project.auth_service.repository.AccountRoleRepository;
import com.project.auth_service.repository.RoleRepository;
import com.project.auth_service.service.AccountRoleService;
import com.project.common_lib_service.exception.SystemError;
import com.project.common_lib_service.exception.SystemException;
import com.project.common_lib_service.utils.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountRoleServiceImpl implements AccountRoleService {

    private final AccountRoleRepository accountRoleRepository;
    private final RoleRepository roleRepository;

    @Override
    public List<String> getRoleNameByEmail(String email) {
        return accountRoleRepository.getRoleNameByEmail(email);
    }

    @Override
    public List<String> getRoleNameByUsername(String username) {
        return accountRoleRepository.getRoleNameByUsername(username);
    }

    @Override
    public void assignDefaultRole(Account account) {
        Role role = roleRepository.findByName(RoleEnum.USER.name())
                .orElseThrow(() -> new SystemException(SystemError.ERROR_010)); // Role not found

        AccountRole accountRole = AccountRole.builder()
                .accountId(account.getId())
                .roleId(role.getId())
                .build();

        accountRoleRepository.save(accountRole);
    }
}

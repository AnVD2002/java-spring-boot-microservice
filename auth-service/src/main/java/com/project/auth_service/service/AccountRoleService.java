package com.project.auth_service.service;

import com.project.auth_service.entity.Account;

import java.util.List;

public interface AccountRoleService {

    List<String> getRoleNameByEmail(String email);

    List<String> getRoleNameByUsername(String username);

    void assignDefaultRole(Account account);

}

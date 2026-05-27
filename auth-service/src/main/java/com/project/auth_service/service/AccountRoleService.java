package com.project.auth_service.service;

import java.util.List;

public interface AccountRoleService {

    List<String> getRoleNameByEmail(String email);

    List<String> getRoleNameByUsername(String username);

}

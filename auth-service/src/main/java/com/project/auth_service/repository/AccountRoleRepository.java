package com.project.auth_service.repository;

import com.project.auth_service.entity.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRole, UUID> {
}

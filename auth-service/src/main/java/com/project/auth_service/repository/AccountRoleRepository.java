package com.project.auth_service.repository;

import com.project.auth_service.entity.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRole, UUID> {

    @Query(value = "select r.name from Account a " +
            "inner join AccountRole ar on ar.accountId = a.id " +
            "inner join Role r on r.id =ar.roleId " +
            "where a.username =:username " +
            "and a.status = 1 and a.deleteAt is null")
    List<String> getRoleNameByUsername(String username);

    @Query(value = "select r.name from Account a " +
            "inner join AccountRole ar on ar.accountId = a.id " +
            "inner join Role r on r.id =ar.roleId " +
            "where a.email =:email " +
            "and a.status = 1 and a.deleteAt is null")
    List<String> getRoleNameByEmail(String email);
}

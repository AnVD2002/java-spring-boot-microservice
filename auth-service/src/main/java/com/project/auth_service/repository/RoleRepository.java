package com.project.auth_service.repository;

import com.project.auth_service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    @Query(value = "select r.name from Account a " +
            "inner join AccountRole ar on ar.accountId = a.id " +
            "inner join Role r on r.id =ar.roleId " +
            "where a.username =:username " +
            "and a.status = 1")
    List<String> getRoleNameByUsername(String username);

    @Query(value = "select r.name from Account a " +
            "inner join AccountRole ar on ar.accountId = a.id " +
            "inner join Role r on r.id =ar.roleId " +
            "where a.username =:email " +
            "and a.status = 1")
    List<String> getRoleNameByEmail(String email);


}

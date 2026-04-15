package com.project.auth_service.repository;

import com.project.auth_service.dto.response.AccountInfoDto;

import com.project.auth_service.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    @Query(value = "select a from Account a " +
            "where a.status = 1 and a.username =:email ")
    Optional<Account> getAccountExisted(String email);

    @Query(value = "select a from Account a where a.username =:username and a.status = 1")
    Optional<Account> getAccountByUsername(String username);

    @Query(value = "select new com.project.auth_service.dto.response.AccountInfoDto(a.id, a.username,a.password, r.name, a.email) from Account a " +
            "inner join AccountRole ar on ar.roleId = a.id " +
            "inner join Role r on ar.roleId = r.id " +
            "where a.status = 1 " +
            "and a.username = :username")
    AccountInfoDto getAccountInfoDtoByUsername(String username);

    @Query(value = "select new com.project.auth_service.dto.response.AccountInfoDto(a.id, a.username,a.password, r.name, a.email) from Account a " +
            "inner join AccountRole ar on ar.roleId = a.id " +
            "inner join Role r on ar.roleId = r.id " +
            "where a.status = 1 " +
            "and a.email = :email")
    AccountInfoDto getAccountInfoDtoByEmail(String email);


    void deleteAccountById(UUID id);

}

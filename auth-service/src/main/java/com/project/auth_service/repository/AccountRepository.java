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
            "where a.status = 1 and a.username =:email and a.deleteAt is null")
    Optional<Account> getAccountExisted(String email);

    @Query(value = "select a from Account a where a.username =:username and a.status = 1 and a.deleteAt is null")
    Optional<Account> getAccountByUsername(String username);

    @Query(value = "select new com.project.auth_service.dto.response.AccountInfoDto(a.id, a.username,a.password, r.name, a.email) from Account a " +
            "inner join AccountRole ar on ar.accountId = a.id " +
            "inner join Role r on ar.roleId = r.id " +
            "where a.status = 1 " +
            "and a.deleteAt is null " +
            "and a.username = :username")
    AccountInfoDto getAccountInfoDtoByUsername(String username);

    @Query(value = "select new com.project.auth_service.dto.response.AccountInfoDto(a.id, a.username,a.password, r.name, a.email) from Account a " +
            "inner join AccountRole ar on ar.accountId = a.id " +
            "inner join Role r on ar.roleId = r.id " +
            "where a.status = 1 " +
            "and a.deleteAt is null " +
            "and a.email = :email")
    AccountInfoDto getAccountInfoDtoByEmail(String email);


    void deleteAccountById(UUID id);

    @Query(value = "select a from Account a where a.email =:email and a.status = 1 and a.deleteAt is null")
    Optional<Account> findByEmail(String email);

}

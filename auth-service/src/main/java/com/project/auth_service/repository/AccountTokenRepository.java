package com.project.auth_service.repository;


import com.project.auth_service.entity.AccountToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountTokenRepository extends JpaRepository<AccountToken, Long> {
}

package com.project.auth_service.repository;

import com.project.auth_service.entity.BlacklistToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BlacklistTokenRepository extends JpaRepository<BlacklistToken, UUID> {
}

package com.project.auth_service.repository;

import com.project.auth_service.entity.DeviceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceLogRepository extends JpaRepository<DeviceLog, UUID> {

    @Query("select dl from DeviceLog dl where dl.userId = :accountId and dl.status = 1")
    List<DeviceLog> findAccountUsingByAccountId(UUID accountId);

    @Query("select dl from DeviceLog dl where dl.userId = :accountId")
    List<DeviceLog> findByAccountId(UUID accountId);

    Optional<DeviceLog> findByDeviceIdAndUserId(UUID deviceId, UUID userId);
}

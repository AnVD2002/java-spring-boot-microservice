package com.project.auth_service.repository;

import com.project.auth_service.entity.DeviceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeviceLogRepository extends JpaRepository<DeviceLog, UUID> {

    @Query(value = "select dl from DeviceLog dl " +
            "inner join AccountToken at on at.userId = dl.userId " +
            "and dl.status = 1 " +
            "and at.isRevoked is false " +
            "and dl.userId =:accountId")
    List<DeviceLog> findAccountUsingByAccountId(UUID accountId);

    @Query(value = "select dl from DeviceLog dl " +
            "inner join AccountToken at on at.userId = dl.userId " +
            "and dl.status = 1 ")
    List<DeviceLog> findByAccountId(UUID accountId);
}

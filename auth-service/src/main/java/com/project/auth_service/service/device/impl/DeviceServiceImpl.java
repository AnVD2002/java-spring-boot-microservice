package com.project.auth_service.service.device.impl;

import com.project.auth_service.config.DeviceProperties;
import com.project.auth_service.entity.DeviceLog;
import com.project.auth_service.repository.DeviceLogRepository;
import com.project.auth_service.service.device.DeviceService;
import com.project.common_lib_service.exception.AuthenticationError;
import com.project.common_lib_service.exception.SystemException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceLogRepository deviceLogRepository;
    private final DeviceProperties deviceProperties;

    /**
     * Get and save deviceId
     *
     * @param deviceId
     * @param accountId
     * @return
     */
    public String getAndSaveDeviceId(String deviceId, UUID accountId) {

        // Known device: client sent a deviceId it previously received
        if (StringUtils.hasText(deviceId)) {
            DeviceLog existing = deviceLogRepository.findByDeviceIdAndUserId(UUID.fromString(deviceId), accountId)
                    .orElse(null);

            if (existing != null) {
                existing.setLastSeenAt(LocalDateTime.now());
                deviceLogRepository.save(existing);
                return existing.getDeviceId().toString();
            }
        }

        // New device: check limit before registering
        List<DeviceLog> activeDevices = deviceLogRepository.findAccountUsingByAccountId(accountId);
        if (activeDevices.size() >= deviceProperties.getMaxPerAccount()) {
            throw new SystemException(AuthenticationError.AUTH_004);
        }

        UUID newDeviceId = UUID.randomUUID();
        DeviceLog newDeviceLog = DeviceLog.builder()
                .userId(accountId)
                .deviceId(newDeviceId)
                .firstSeenAt(LocalDateTime.now())
                .lastSeenAt(LocalDateTime.now())
                .build();

        deviceLogRepository.save(newDeviceLog);
        return newDeviceId.toString();
    }
}

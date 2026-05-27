package com.project.auth_service.service.device.impl;

import com.project.auth_service.entity.DeviceLog;
import com.project.auth_service.repository.DeviceLogRepository;
import com.project.auth_service.service.device.DeviceService;
import com.project.common_lib_service.exception.AuthenticationError;
import com.project.common_lib_service.exception.SystemException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceLogRepository deviceLogRepository;

    /**
     * Get and save deviceId
     *
     * @param deviceId
     * @param accountId
     * @return
     */
    public String getAndSaveDeviceId(String deviceId, String accountId) {

        DeviceLog newDeviceLog = new DeviceLog();

        UUID newDeviceId = UUID.randomUUID();

        List<DeviceLog> devicesByAccountId = deviceLogRepository.findByAccountId(UUID.fromString(accountId));

        if (!CollectionUtils.isEmpty(devicesByAccountId)) {
            List<UUID> deviceIdLists = devicesByAccountId.stream().map(DeviceLog::getDeviceId).toList();

            List<String> deviceIdListString = deviceIdLists.stream()
                    .map(UUID::toString)
                    .toList();

            //If deviceId and accountId are provided, check if deviceId exists for the account
            if (StringUtils.hasText(deviceId) && StringUtils.hasText(accountId)) {
                List<DeviceLog> deviceUsing = deviceLogRepository.findAccountUsingByAccountId(UUID.fromString(accountId));

                // Limit to 3 devices
                if (deviceUsing.size() > 3) {
                    throw new SystemException(AuthenticationError.AUTH_002);
                }

                // If deviceId does not exist, create new device log
                if (!deviceIdListString.contains(deviceId)) {
                    newDeviceLog = DeviceLog.builder()
                            .userId(UUID.fromString(accountId))
                            .deviceId(newDeviceId)
                            .lastSeenAt(LocalDateTime.now())
                            .firstSeenAt(LocalDateTime.now())
                            .build();
                } else {
                    for (DeviceLog deviceLog : devicesByAccountId) {
                        if (deviceId.equals(deviceLog.getId().toString())) {
                            deviceLog.setLastSeenAt(LocalDateTime.now());
                            newDeviceLog = deviceLog;
                            newDeviceId = deviceLog.getDeviceId();
                        }
                    }
                }

            }
        } else {
            newDeviceLog = DeviceLog.builder()
                    .userId(UUID.fromString(accountId))
                    .deviceId(newDeviceId)
                    .lastSeenAt(LocalDateTime.now())
                    .firstSeenAt(LocalDateTime.now())
                    .build();
        }

        deviceLogRepository.save(newDeviceLog);

        return newDeviceId.toString();
    }
}

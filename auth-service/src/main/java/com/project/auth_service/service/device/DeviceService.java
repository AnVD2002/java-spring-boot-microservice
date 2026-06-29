package com.project.auth_service.service.device;

import java.util.UUID;

public interface DeviceService {
    UUID getAndSaveDeviceId(String deviceId, UUID accountId);
}

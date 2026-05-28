package com.project.auth_service.service.device;

import java.util.UUID;

public interface DeviceService {
    String getAndSaveDeviceId(String deviceId, UUID accountId);
}

package com.project.auth_service.service.device.impl;

import com.project.auth_service.config.DeviceProperties;
import com.project.auth_service.entity.DeviceLog;
import com.project.auth_service.enums.DeviceStatus;
import com.project.auth_service.repository.DeviceLogRepository;
import com.project.common_lib_service.exception.SystemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeviceServiceImplTest {

    private DeviceLogRepository deviceLogRepository;
    private DeviceServiceImpl deviceService;

    @BeforeEach
    void setUp() {
        deviceLogRepository = mock(DeviceLogRepository.class);
        DeviceProperties deviceProperties = new DeviceProperties();
        deviceProperties.setMaxPerAccount(3);
        deviceService = new DeviceServiceImpl(deviceLogRepository, deviceProperties);
    }

    @Test
    void knownDeviceUpdatesLastSeenAndReturnsSameDeviceId() {
        UUID accountId = UUID.randomUUID();
        UUID deviceId = UUID.randomUUID();
        DeviceLog existingDevice = DeviceLog.builder()
                .userId(accountId)
                .deviceId(deviceId)
                .status(DeviceStatus.ACTIVE.getValue())
                .build();

        when(deviceLogRepository.findByDeviceIdAndUserId(deviceId, accountId)).thenReturn(Optional.of(existingDevice));

        UUID resolvedDeviceId = deviceService.getAndSaveDeviceId(deviceId.toString(), accountId);

        assertThat(resolvedDeviceId).isEqualTo(deviceId);
        assertThat(existingDevice.getLastSeenAt()).isNotNull();
        verify(deviceLogRepository).save(existingDevice);
        verify(deviceLogRepository, never()).findAccountUsingByAccountId(any());
    }

    @Test
    void newDeviceIsSavedAndReturnedWhenAccountIsUnderLimit() {
        UUID accountId = UUID.randomUUID();
        when(deviceLogRepository.findAccountUsingByAccountId(accountId)).thenReturn(List.of());

        UUID resolvedDeviceId = deviceService.getAndSaveDeviceId(null, accountId);

        ArgumentCaptor<DeviceLog> deviceLogCaptor = ArgumentCaptor.forClass(DeviceLog.class);
        verify(deviceLogRepository).save(deviceLogCaptor.capture());

        DeviceLog savedDevice = deviceLogCaptor.getValue();
        assertThat(resolvedDeviceId).isEqualTo(savedDevice.getDeviceId());
        assertThat(savedDevice.getUserId()).isEqualTo(accountId);
        assertThat(savedDevice.getStatus()).isEqualTo(DeviceStatus.ACTIVE.getValue());
        assertThat(savedDevice.getFirstSeenAt()).isNotNull();
        assertThat(savedDevice.getLastSeenAt()).isNotNull();
    }

    @Test
    void newDeviceIsRejectedWhenAccountReachedLimit() {
        UUID accountId = UUID.randomUUID();
        when(deviceLogRepository.findAccountUsingByAccountId(accountId)).thenReturn(List.of(
                DeviceLog.builder().build(),
                DeviceLog.builder().build(),
                DeviceLog.builder().build()
        ));

        assertThatThrownBy(() -> deviceService.getAndSaveDeviceId(null, accountId))
                .isInstanceOf(SystemException.class);

        verify(deviceLogRepository, never()).save(any());
    }
}

package com.project.auth_service.enums;

import lombok.Getter;

@Getter
public enum DeviceStatus {
    INACTIVE(0),
    ACTIVE(1),
    BLOCKED(2),
    TRUSTED(3),
    REMOVED(4);

    private final int value;

    DeviceStatus(int value) {
        this.value = value;
    }

    public static DeviceStatus fromValue(int value) {
        for (DeviceStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown device status value: " + value);
    }
}

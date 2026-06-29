package com.project.user_service.enums;

import lombok.Getter;

@Getter
public enum UserProfileStatus {
    INACTIVE(0),
    ACTIVE(1),
    SUSPENDED(2),
    DELETED(4);

    private final int value;

    UserProfileStatus(int value) {
        this.value = value;
    }

    public static UserProfileStatus fromValue(int value) {
        for (UserProfileStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown user profile status value: " + value);
    }
}

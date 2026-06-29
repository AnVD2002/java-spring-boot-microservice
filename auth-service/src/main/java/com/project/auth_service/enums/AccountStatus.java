package com.project.auth_service.enums;

import lombok.Getter;

@Getter
public enum AccountStatus {
    PENDING_EMAIL_VERIFICATION(0),
    ACTIVE(1),
    SUSPENDED(2),
    LOCKED(3),
    DELETED(4);

    private final int value;

    AccountStatus(int value) {
        this.value = value;
    }

    public static AccountStatus fromValue(int value) {
        for (AccountStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown account status value: " + value);
    }
}

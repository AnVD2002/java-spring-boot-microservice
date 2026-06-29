package com.project.user_service.enums;

import lombok.Getter;

@Getter
public enum MembershipStatus {
    INVITED(0),
    ACTIVE(1),
    SUSPENDED(2),
    COMPLETED(3),
    REMOVED(4);

    private final int value;

    MembershipStatus(int value) {
        this.value = value;
    }

    public static MembershipStatus fromValue(int value) {
        for (MembershipStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown membership status value: " + value);
    }
}

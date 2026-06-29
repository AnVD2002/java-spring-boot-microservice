package com.project.user_service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
}

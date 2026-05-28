package com.project.user_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private UUID id;
    private UUID accountId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private Integer status;
    private Instant insertedAt;
}

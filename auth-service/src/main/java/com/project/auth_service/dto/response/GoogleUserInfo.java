package com.project.auth_service.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleUserInfo {
    private String sub;       // Google user id
    private String name;      // Full name
    private String givenName;
    private String familyName;
    private String picture;   // avatar URL
    private String email;
    private Boolean emailVerified;
}

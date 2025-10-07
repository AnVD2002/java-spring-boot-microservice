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
    private String given_name;
    private String family_name;
    private String picture;   // avatar URL
    private String email;
    private Boolean email_verified;
    private String locale;
}

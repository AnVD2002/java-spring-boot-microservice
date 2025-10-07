package com.project.auth_service.dto.event;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailCodeEvent {
    private String email;
    private String code;
}



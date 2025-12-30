package com.project.auth_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "auth.token")
public class AuthTokenProperties {
    private Refresh refresh = new Refresh();

    @Getter
    @Setter
    public static class Refresh {
        private String redisPrefix;
        private long ttlDays;
    }
}

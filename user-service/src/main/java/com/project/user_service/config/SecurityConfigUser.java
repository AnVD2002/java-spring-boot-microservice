package com.project.user_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.common_lib_service.security.BaseSecurityConfig;
import com.project.common_lib_service.security.DefaultAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfigUser {

    @Value("${internal.secret}")
    private String internalSecret;

    @Bean
    public DefaultAuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
        return new DefaultAuthenticationEntryPoint(
                objectMapper,
                401,
                "Authentication required or invalid token"
        );
    }

    @Bean
    public SecurityFilterChain authSecurityFilterChain(
            HttpSecurity http,
            DefaultAuthenticationEntryPoint entryPoint
    ) throws Exception {
        http.securityMatcher("/api/v1/users/**");
        BaseSecurityConfig.applyCommon(http, entryPoint, internalSecret);
        http.authorizeHttpRequests(auth -> auth.anyRequest().authenticated());
        return http.build();
    }
}

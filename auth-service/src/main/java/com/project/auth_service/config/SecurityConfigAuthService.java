package com.project.auth_service.config;

import com.project.common_lib_service.security.BaseSecurityConfig;
import com.project.common_lib_service.security.DefaultAuthenticationEntryPoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfigAuthService {

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
    @Order(1)
    SecurityFilterChain publicEndpoints(HttpSecurity http) throws Exception {
        http
                .securityMatcher(
                        "/api/v1/auth/login/**",
                        "/api/v1/auth/refresh",
                        "/api/v1/auth/logout",
                        "/api/v1/auth/normal/register",
                        "/api/v1/auth/google/url",
                        "/api/v1/auth/google/callback",
                        "/api/v1/auth/google/login",
                        "/api/v1/auth/google/register",
                        "/api/v1/auth/forgot-password",
                        "/api/v1/auth/verify-otp",
                        "/api/v1/auth/reset-password",
                        "/api/v1/test/public"
                )
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain authenticatedEndpoints(HttpSecurity http, DefaultAuthenticationEntryPoint entryPoint) throws Exception {
        BaseSecurityConfig.applyCommon(http, entryPoint, internalSecret);
        http
                .securityMatcher("/api/v1/auth/**", "/api/v1/test/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}

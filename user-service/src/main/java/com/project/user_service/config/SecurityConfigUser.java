package com.project.user_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.common_lib_service.security.BaseSecurityConfig;
import com.project.common_lib_service.security.DefaultAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfigUser {

    private final UserAuthenticationFilter userAuthenticationFilter;

    public SecurityConfigUser(UserAuthenticationFilter userAuthenticationFilter) {
        this.userAuthenticationFilter = userAuthenticationFilter;
    }

    @Bean
    public DefaultAuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
        return new DefaultAuthenticationEntryPoint(
                objectMapper,
                401,
                "Bạn chưa đăng nhập hoặc token không hợp lệ"
        );
    }

    @Bean
    public SecurityFilterChain authSecurityFilterChain(
            HttpSecurity http,
            DefaultAuthenticationEntryPoint entryPoint
    ) throws Exception {

        http.securityMatcher("/api/v1/user/**");

        BaseSecurityConfig.applyCommon(http, entryPoint);

        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .addFilterBefore(userAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

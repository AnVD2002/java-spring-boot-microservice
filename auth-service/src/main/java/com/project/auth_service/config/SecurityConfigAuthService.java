package com.project.auth_service.config;

import com.project.common_lib_service.config.JwtAuthenticationFilter;
import com.project.common_lib_service.config.SecurityConfigBase;
import com.project.common_lib_service.config.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfigAuthService extends SecurityConfigBase {

    public SecurityConfigAuthService(ClientRegistrationRepository clientRegistrationRepository,
                                     JwtAuthenticationFilter jwtAuthenticationFilter,
                                     SecurityProperties securityProperties) {
        super(clientRegistrationRepository, jwtAuthenticationFilter, securityProperties);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http = baseConfig(http); // dùng config chung từ base

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/auth/register", "/auth/refresh-token").permitAll()
                .requestMatchers(securityProperties.getMergedWhitelist().toArray(new String[0])).permitAll()
                .anyRequest().authenticated()
        );

        return http.build();
    }
}


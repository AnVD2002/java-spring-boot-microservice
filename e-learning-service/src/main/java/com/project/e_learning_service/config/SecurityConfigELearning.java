package com.project.e_learning_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.common_lib_service.security.BaseSecurityConfig;
import com.project.common_lib_service.security.DefaultAuthenticationEntryPoint;
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
public class SecurityConfigELearning {

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
    public SecurityFilterChain publicDocsFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain eLearningSecurityFilterChain(
            HttpSecurity http,
            DefaultAuthenticationEntryPoint entryPoint
    ) throws Exception {
        http.securityMatcher("/api/v1/elearning/**", "/api/v1/eLearning/**");
        BaseSecurityConfig.applyCommon(http, entryPoint, internalSecret);
        http.authorizeHttpRequests(auth -> auth.anyRequest().authenticated());
        return http.build();
    }
}

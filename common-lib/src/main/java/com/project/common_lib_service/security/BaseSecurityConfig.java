package com.project.common_lib_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class BaseSecurityConfig {

    public static void applyCommon(HttpSecurity http,
                                   DefaultAuthenticationEntryPoint entryPoint,
                                   String internalSecret) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(new DefaultAccessDeniedHandler(objectMapper))
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(new InternalAuthFilter(internalSecret),
                        UsernamePasswordAuthenticationFilter.class);
    }
}

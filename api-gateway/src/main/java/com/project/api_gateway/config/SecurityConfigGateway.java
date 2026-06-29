package com.project.api_gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfigGateway {

    private final JwtAuthenticationWebFluxFilter jwtAuthenticationWebFluxFilter;
    private final ObjectMapper objectMapper;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .exceptionHandling(e -> e.authenticationEntryPoint(customAuthenticationEntryPoint()))
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .matchers(ServerWebExchangeMatchers.pathMatchers(
                                "/api/v1/auth/login/**",
                                "/api/v1/auth/refresh",
                                "/api/v1/auth/logout",
                                "/api/v1/auth/normal/register",
                                "/api/v1/auth/normal/confirm",
                                "/api/v1/auth/google/url",
                                "/api/v1/auth/google/callback",
                                "/api/v1/auth/google/login",
                                "/api/v1/auth/google/register",
                                "/api/v1/auth/forgot-password",
                                "/api/v1/auth/verify-otp",
                                "/api/v1/auth/reset-password",
                                "/api/v1/test/public",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**"
                        ))
                        .permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterBefore(jwtAuthenticationWebFluxFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public ServerAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (exchange, ex) -> {
            var response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            try {
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("code", 401);
                body.put("message", "Unauthorized or invalid token");
                body.put("url", exchange.getRequest().getPath().value());

                byte[] bytes = objectMapper.writeValueAsBytes(body);
                var buffer = response.bufferFactory().wrap(bytes);
                return response.writeWith(Mono.just(buffer));
            } catch (Exception e) {
                return response.setComplete();
            }
        };
    }
}

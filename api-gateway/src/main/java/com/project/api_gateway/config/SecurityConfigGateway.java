package com.project.api_gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfigGateway {

    private final JwtAuthenticationWebFluxFilter jwtAuthenticationWebFluxFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .exceptionHandling(e -> e.authenticationEntryPoint(customAuthenticationEntryPoint()))
                .authorizeExchange(exchanges -> exchanges
                        .matchers(ServerWebExchangeMatchers.pathMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/test/public"
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

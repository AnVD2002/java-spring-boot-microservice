package com.project.api_gateway_service.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfigGateway {

    private final JwtAuthenticationWebFluxFilter jwtAuthenticationWebFluxFilter;

    public SecurityConfigGateway(JwtAuthenticationWebFluxFilter jwtAuthenticationWebFluxFilter) {
        this.jwtAuthenticationWebFluxFilter = jwtAuthenticationWebFluxFilter;
    }

    // Đây là bean quan trọng
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())

                .authorizeExchange(exchanges -> exchanges
                        .matchers(ServerWebExchangeMatchers.pathMatchers("/api/v1/auth/**"))
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
            var buffer = response.bufferFactory()
                    .wrap("{\"code\":401,\"message\":\"Unauthorized or invalid token\"}".getBytes());
            return response.writeWith(Mono.just(buffer));
        };
    }
}


package com.project.api_gateway_service.config;

import com.project.common_lib_service.config.JwtProvider;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthGatewayFilter extends AbstractGatewayFilterFactory<JwtAuthGatewayFilter.Config> {

    private final JwtProvider jwtProvider;

    public JwtAuthGatewayFilter(JwtProvider jwtProvider) {
        super(Config.class);
        this.jwtProvider = jwtProvider;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            String authHeader = request.getHeaders().getFirst("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return this.onError(exchange, "Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);

            // validate token
            if (jwtProvider.isTokenExpired(token)) {
                return this.onError(exchange, "Token expired");
            }

            if (!jwtProvider.isSignatureValid(token)) {
                return this.onError(exchange, "Invalid token signature");
            }

            // extract claims
            String username = jwtProvider.extractUserName(token);
            UUID accountId = jwtProvider.extractAccountId(token);
            List<String> role = jwtProvider.extractUserRole(token);

            // mutate request to add headers
            ServerHttpRequest mutatedRequest = exchange.getRequest()
                    .mutate()
                    .header("X-Username", username != null ? username : "")
                    .header("X-AccountId", accountId != null ? accountId.toString() : "")
                    .header("X-Role", role != null && !role.isEmpty() ? String.join(",", role) : "")
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        String errorResponse = String.format("{\"error\": \"Unauthorized\", \"message\": \"%s\"}", err);
        DataBuffer buffer = response.bufferFactory().wrap(errorResponse.getBytes(StandardCharsets.UTF_8));
        response.getHeaders().add("Content-Type", "application/json");

        return response.writeWith(Mono.just(buffer));
    }

    public static class Config {
        // Configuration properties can be added here if needed
        // For example: excluded paths, custom headers, etc.
    }
}

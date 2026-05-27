package com.project.api_gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.common_lib_service.dto.CustomUserPrincipal;
import com.project.common_lib_service.jwt.JwtProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.project.common_lib_service.utils.SecurityHeaders.*;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationWebFluxFilter implements WebFilter {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Value("${internal.secret}")
    private String internalSecret;

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);

        try {
            if (!jwtProvider.isSignatureValid(token) || jwtProvider.isTokenExpired(token)) {
                return unauthorized(exchange, "Token is invalid or expired");
            }

            String username = jwtProvider.extractUserName(token);
            UUID accountId = jwtProvider.extractAccountId(token);
            List<String> roles = jwtProvider.extractUserRole(token);

            if (username == null || accountId == null) {
                return unauthorized(exchange, "Invalid token: missing claims");
            }

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .toList();

            CustomUserPrincipal principal = CustomUserPrincipal.builder()
                    .userId(accountId)
                    .username(username)
                    .roles(roles)
                    .build();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);

            SecurityContext context = new SecurityContextImpl(authentication);

            return chain.filter(
                    exchange.mutate()
                            .request(builder -> builder
                                    .header(USER_ID, accountId.toString())
                                    .header(USERNAME, username)
                                    .header(ROLES, String.join(",", roles))
                                    .header(AUTH_TOKEN, token)
                                    .header(INTERNAL_SECRET, internalSecret)
                            )
                            .build()
            ).contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));

        } catch (Exception e) {
            return unauthorized(exchange, "Invalid token");
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("code", 401);
            body.put("message", message);
            body.put("url", exchange.getRequest().getPath().value());

            byte[] bytes = objectMapper.writeValueAsBytes(body);
            var buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception ex) {
            return response.setComplete();
        }
    }
}

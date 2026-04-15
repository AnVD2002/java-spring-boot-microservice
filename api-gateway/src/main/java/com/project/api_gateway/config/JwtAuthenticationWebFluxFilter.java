package com.project.api_gateway.config;

import com.project.common_lib_service.jwt.JwtProvider;
import com.project.common_lib_service.dto.CustomUserPrincipal;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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

import java.util.List;
import java.util.UUID;

import static com.project.common_lib_service.utils.SecurityHeaders.*;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationWebFluxFilter implements WebFilter {

    private final JwtProvider jwtProvider;

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange,@NonNull WebFilterChain chain) {
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);

        try {
            // 1. Verify chữ ký + parse token
            if (!jwtProvider.isSignatureValid(token) || jwtProvider.isTokenExpired(token)) {
                return chain.filter(exchange);
            }

            // 2. Extract thông tin user
            String username = jwtProvider.extractUserName(token);
            UUID accountId = jwtProvider.extractAccountId(token);
            List<String> roles = jwtProvider.extractUserRole(token);

            if (username == null || accountId == null) {
                return chain.filter(exchange);
            }

            // 3. Map role → GrantedAuthority
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .toList();

            CustomUserPrincipal principal = CustomUserPrincipal.builder()
                    .userId(accountId)
                    .username(username)
                    .roles(roles)
                    .build();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            authorities
                    );

            SecurityContext context = new SecurityContextImpl(authentication);

            // 4. Set vào ReactiveSecurityContext
            return chain.filter(
                    exchange.mutate()
                            .request(builder -> builder
                                    .header(USER_ID, accountId.toString())
                                    .header(USERNAME, username)
                                    .header(ROLES, String.join(",", roles))
                            )
                            .build()
            ).contextWrite(
                    ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context))
            );

        } catch (Exception e) {
            // Token lỗi → cho qua hoặc log nếu muốn
            return chain.filter(exchange);
        }
    }
}


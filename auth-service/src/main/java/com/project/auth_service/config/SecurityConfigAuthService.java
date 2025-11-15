package com.project.auth_service.config;

//@Configuration
//@EnableWebSecurity
//public class SecurityConfigAuthService extends SecurityConfigBase {
//
//    public SecurityConfigAuthService(ClientRegistrationRepository clientRegistrationRepository,
//                                     JwtAuthenticationFilter jwtAuthenticationFilter) {
//        super(clientRegistrationRepository, jwtAuthenticationFilter);
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http = baseConfig(http); // dùng config chung từ base
//
//        http.authorizeHttpRequests(auth -> auth
//                .requestMatchers("/auth/v1", "/auth/register", "/auth/refresh-token").permitAll()
//                .anyRequest().authenticated()
//        );
//
//        return http.build();
//    }
//}


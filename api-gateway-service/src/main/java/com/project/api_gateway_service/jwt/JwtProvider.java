package com.project.api_gateway_service.jwt;

import com.project.api_gateway_service.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private PublicKey publicKey;

    /**
     * Load public key từ file public.pem
     */
    private PublicKey getPublicKey() {
        if (publicKey == null) {
            try {
                // 1. Load file theo path từ JwtProperties
                Resource resource;
                String keyPath = jwtProperties.getPublicKey();

                if (keyPath.startsWith("classpath:")) {
                    resource = new ClassPathResource(keyPath.replace("classpath:", ""));
                } else {
                    resource = new FileSystemResource(keyPath);
                }

                // 2. Đọc file
                InputStream inputStream = resource.getInputStream();
                String publicKeyPEM = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                // 3. Loại bỏ header/footer
                publicKeyPEM = publicKeyPEM
                        .replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
                        .replaceAll("\\s", "");

                // 4. Decode Base64
                byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);

                // 5. Tạo PublicKey object
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                publicKey = keyFactory.generatePublic(keySpec);

            } catch (Exception e) {
                throw new RuntimeException("Error loading public key", e);
            }
        }
        return publicKey;
    }

    /**
     * Extract claim token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claim from token
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Check expire time
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Check sign token
     */
    public boolean isSignatureValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getPublicKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractUserName(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long extractAccountId(String token) {
        Object userId = extractAllClaims(token).get("accountId");
        if (userId == null) return null;
        return Long.parseLong(userId.toString());
    }

    public String extractUserRole(String token) {
        Object role = extractAllClaims(token).get("role");
        return role != null ? role.toString() : null;
    }
}

package com.project.common_lib_service.config;

import com.project.common_lib_service.dto.RefreshTokenInfo;
import com.project.common_lib_service.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    /**
     * Đọc nội dung PEM key file (bỏ header/footer)
     */
    private String readKeyFromFile(String path, String beginMarker, String endMarker) throws IOException {
        Resource resource;

        if (path.startsWith("classpath:")) {
            resource = new ClassPathResource(path.replace("classpath:", ""));
        } else {
            resource = new FileSystemResource(path);
        }

        try (InputStream inputStream = resource.getInputStream()) {
            String key = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return key
                    .replace(beginMarker, "")
                    .replace(endMarker, "")
                    .replaceAll("\\s+", "");
        }
    }

    /**
     * Load private key từ file PEM
     */
    private PrivateKey getPrivateKey() {
        if (privateKey == null) {
            try {
                String privateKeyPEM = readKeyFromFile(
                        jwtProperties.getPrivateKey(),
                        "-----BEGIN PRIVATE KEY-----",
                        "-----END PRIVATE KEY-----"
                );

                byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                privateKey = keyFactory.generatePrivate(keySpec);
            } catch (Exception e) {
                throw new RuntimeException("Error loading private key", e);
            }
        }
        return privateKey;
    }

    /**
     * Load public key từ file PEM
     */
    private PublicKey getPublicKey() {
        if (publicKey == null) {
            try {
                String publicKeyPEM = readKeyFromFile(
                        jwtProperties.getPublicKey(),
                        "-----BEGIN PUBLIC KEY-----",
                        "-----END PUBLIC KEY-----"
                );

                byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
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
     * Generate Access Token
     */
    public String generateAccessToken(String username, UUID accountId, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("accountId", accountId);
        claims.put("roles", roles);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    /**
     * Generate Refresh Token
     */
    public String generateRefreshToken(String username, UUID accountId, List<String> roles) {
        String jti = UUID.randomUUID().toString();

        Map<String, Object> claims = new HashMap<>();
        claims.put("jti", jti);

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshExpiration()))
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();

        RefreshTokenInfo tokenInfo = new RefreshTokenInfo(
                jti, username, accountId, roles,
                System.currentTimeMillis() + jwtProperties.getRefreshExpiration()
        );

        refreshTokenRepository.save(tokenInfo);

        return refreshToken;
    }

    /**
     * Parse claims từ token
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

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

    public UUID extractAccountId(String token) {
        Object userId = extractAllClaims(token).get("accountId");
        if (userId == null) return null;
        return UUID.fromString(userId.toString());
    }

    public List<String> extractUserRole(String token) {
        Claims claims = extractAllClaims(token);
        Object rolesObj = claims.get("roles");

        if (rolesObj instanceof List<?>) {
            return ((List<?>) rolesObj).stream()
                    .map(Object::toString)
                    .toList();
        }

        return Collections.emptyList();
    }
}

package com.project.auth_service.jwt;

import com.project.auth_service.config.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    private PrivateKey privateKey;

    private PrivateKey getPrivateKey() {
        if (privateKey == null) {
            try {
                String privateKeyPEM = getPrivateKeyPEM();

                // 4. Decode Base64
                byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);

                // 5. Tạo PrivateKey object
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                privateKey = keyFactory.generatePrivate(keySpec);

            } catch (Exception e) {
                throw new RuntimeException("Error loading private key", e);
            }
        }
        return privateKey;
    }

    private String getPrivateKeyPEM() throws IOException {
        Resource resource;
        String keyPath = jwtProperties.getPrivateKey();

        if (keyPath.startsWith("classpath:")) {
            resource = new ClassPathResource(keyPath.replace("classpath:", ""));
        } else {
            resource = new FileSystemResource(keyPath);
        }

        InputStream inputStream = resource.getInputStream();
        String privateKeyPEM = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        privateKeyPEM = privateKeyPEM
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        return privateKeyPEM;
    }

    /**
     * Generate Access Token
     */
    public String generateAccessToken(String username, Long accountId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("accountId", accountId);
        claims.put("role", role);

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
    public String generateRefreshToken(String username, PrivateKey privateKey, long refreshExpirationMillis) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshExpiration()))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }


}

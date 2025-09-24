package com.project.api_gateway_service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

public class JwtProvider {

    private static final String SECRET_KEY = "e82c73692e6fa99b1770cfd6605bfc5b9ec3a12b362d9de5459a2612191497c4";
    public static final long JWT_EXPIRATION = 3600000L; // 1 giờ
    public static final long REFRESH_TOKEN_EXPIRATION = 2592000000L;

    /**
     * Extracts the username (subject) from the JWT token.
     * @param token
     * @return
     */
    public String ExtractUserName(String token){
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the JWT token using the provided claims resolver function.
     * @param token
     * @param claimsResolver
     * @return
     * @param <T>
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Checks if the JWT token is expired.
     * @param token
     * @return
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts all claims from the JWT token.
     * @param token
     * @return
     */
    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extracts the expiration date from the JWT token.
     * @param token
     * @return
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    /**
     * Generates a JWT token for the given username.
     * @return
     */
    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Validates the signature of the JWT token.
     * @param token
     * @return
     */
    public boolean isSignatureValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Extracts the username (subject) from the JWT token.
     * @param token
     * @return
     */
    public String extractUserName(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extracts the account ID from the JWT token.
     * @param token
     * @return
     */
    public Long extractAccountId(String token) {
        Object userId = extractAllClaims(token).get("accountId");
        if (userId == null) return null;
        return Long.parseLong(userId.toString());
    }

    /**
     * Extracts the user role from the JWT token.
     * @param token
     * @return
     */
    public String extractUserRole(String token) {
        Object role = extractAllClaims(token).get("role");
        return role != null ? role.toString() : null;
    }


}

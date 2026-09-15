package com.oilcommerce.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String jwtSecret;

    @Value("${jwt.expiration:900000}")
    private long accessTokenExpiry;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshTokenExpiry;

    @Value("${jwt.issuer:oil-commerce-api}")
    private String issuer;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(
                java.util.Base64.getEncoder().encodeToString(jwtSecret.getBytes()));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(UUID userId, String email, String role) {
        return buildToken(userId.toString(), email, role, accessTokenExpiry, "access");
    }

    public String generateRefreshToken(UUID userId) {
        return buildToken(userId.toString(), null, null, refreshTokenExpiry, "refresh");
    }

    private String buildToken(String subject, String email, String role, long expiry, String type) {
        Map<String, Object> claims = new java.util.HashMap<>();
        if (email != null) claims.put("email", email);
        if (role != null) claims.put("role", role);
        claims.put("type", type);

        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiry))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return (String) getClaims(token).get("role");
    }

    public String getEmailFromToken(String token) {
        return (String) getClaims(token).get("email");
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

package com.kriterion.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultJwtTokenService implements JwtTokenService {

    private final JwtProperties properties;

    /**
     * Generates a SecretKey for HMAC-SHA256 signing.
     * Compatible with JJWT 0.12.x API.
     * Keys.hmacShaKeyFor() returns javax.crypto.SecretKey, not java.security.Key.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = properties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateAccessToken(String subject) {
        long minutes = properties.getAccessTokenExpirationMinutes();
        Date now = new Date();
        Date exp = new Date(now.getTime() + minutes * 60L * 1000L);
        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(exp)
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String generateRefreshToken(String subject) {
        long days = properties.getRefreshTokenExpirationDays();
        Date now = new Date();
        Date exp = new Date(now.getTime() + days * 24L * 60L * 60L * 1000L);
        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(exp)
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String extractSubject(String token) {
        try {
            return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload().getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}

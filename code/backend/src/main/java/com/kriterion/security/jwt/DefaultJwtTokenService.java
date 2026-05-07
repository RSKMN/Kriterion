package com.kriterion.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultJwtTokenService implements JwtTokenService {

    private final JwtProperties properties;

    private Key getSigningKey() {
        byte[] keyBytes = properties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateAccessToken(String subject) {
        long minutes = properties.getAccessTokenExpirationMinutes();
        Date now = new Date();
        Date exp = new Date(now.getTime() + minutes * 60L * 1000L);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateRefreshToken(String subject) {
        long days = properties.getRefreshTokenExpirationDays();
        Date now = new Date();
        Date exp = new Date(now.getTime() + days * 24L * 60L * 60L * 1000L);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String extractSubject(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
package com.kriterion.security.jwt;

import org.springframework.stereotype.Service;

@Service
public class DefaultJwtTokenService implements JwtTokenService {

    @Override
    public String generateAccessToken(String subject) {
        throw new UnsupportedOperationException("JWT token generation is not implemented yet");
    }

    @Override
    public String generateRefreshToken(String subject) {
        throw new UnsupportedOperationException("JWT token generation is not implemented yet");
    }

    @Override
    public String extractSubject(String token) {
        throw new UnsupportedOperationException("JWT token parsing is not implemented yet");
    }

    @Override
    public boolean isTokenValid(String token) {
        throw new UnsupportedOperationException("JWT token validation is not implemented yet");
    }
}

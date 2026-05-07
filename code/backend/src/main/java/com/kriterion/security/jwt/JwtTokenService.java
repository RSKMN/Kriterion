package com.kriterion.security.jwt;

public interface JwtTokenService {
    String generateAccessToken(String subject);

    String generateRefreshToken(String subject);

    String extractSubject(String token);

    boolean isTokenValid(String token);
}

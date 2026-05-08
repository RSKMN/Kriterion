package com.kriterion.service;

import com.kriterion.dto.auth.AuthResponse;
import com.kriterion.entity.User;
import com.kriterion.repository.RefreshTokenRepository;
import com.kriterion.repository.UserRepository;
import com.kriterion.security.jwt.JwtProperties;
import com.kriterion.security.jwt.JwtTokenService;
import com.kriterion.security.ratelimit.RateLimiter;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthServiceLoginTest {

    private UserRepository userRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenService jwtTokenService;
    private JwtProperties jwtProperties;
    private RateLimiter rateLimiter;
    private AuthService authService;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        refreshTokenRepository = Mockito.mock(RefreshTokenRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        jwtTokenService = Mockito.mock(JwtTokenService.class);
        jwtProperties = new JwtProperties();
        jwtProperties.setRefreshTokenExpirationDays(7);
        rateLimiter = Mockito.mock(RateLimiter.class);
        authService = new AuthService(userRepository, passwordEncoder, jwtTokenService, refreshTokenRepository, jwtProperties, rateLimiter);
    }

    @Test
    void login_success() {
        User user = new User();
        user.setId(2L);
        user.setEmail("jane@example.com");
        user.setPasswordHash("hashed");
        Mockito.when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches("pass", "hashed")).thenReturn(true);
        Mockito.when(jwtTokenService.generateAccessToken("2")).thenReturn("access");
        Mockito.when(jwtTokenService.generateRefreshToken("2")).thenReturn("refresh");
        Mockito.when(refreshTokenRepository.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));

        AuthResponse res = authService.login("jane@example.com", "pass");
        Assertions.assertEquals("access", res.getAccessToken());
        Assertions.assertEquals("refresh", res.getRefreshToken());
        Assertions.assertEquals(2L, res.getUser().getId());
    }

    @Test
    void login_invalid_credentials() {
        Mockito.when(userRepository.findByEmail("x@example.com")).thenReturn(Optional.empty());
        try {
            authService.login("x@example.com", "wrong");
            Assertions.fail("Expected ApiException");
        } catch (com.kriterion.exception.ApiException ex) {
            Assertions.assertEquals(org.springframework.http.HttpStatus.UNAUTHORIZED, ex.getStatus());
        }
    }
}

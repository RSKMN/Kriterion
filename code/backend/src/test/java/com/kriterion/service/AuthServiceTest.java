package com.kriterion.service;

import com.kriterion.dto.auth.RegisterRequest;
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

public class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenService jwtTokenService;
    private RefreshTokenRepository refreshTokenRepository;
    private JwtProperties jwtProperties;
    private RateLimiter rateLimiter;
    private AuthService authService;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        jwtTokenService = Mockito.mock(JwtTokenService.class);
        refreshTokenRepository = Mockito.mock(RefreshTokenRepository.class);
        jwtProperties = new JwtProperties();
        jwtProperties.setRefreshTokenExpirationDays(7);
        rateLimiter = Mockito.mock(RateLimiter.class);
        authService = new AuthService(userRepository, passwordEncoder, jwtTokenService, refreshTokenRepository, jwtProperties, rateLimiter);
    }

    @Test
    void register_successful() {
        RegisterRequest req = new RegisterRequest();
        req.setFullName("John Doe");
        req.setEmail("john@example.com");
        req.setPassword("StrongPass1");

        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("hashed");
        User saved = new User();
        saved.setId(1L);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(saved);

        Long id = authService.register(req);
        Assertions.assertEquals(1L, id);
    }

    // Additional tests: duplicate email, invalid payload should be covered by controller validation
}

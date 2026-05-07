package com.kriterion.service;

import com.kriterion.dto.auth.RegisterRequest;
import com.kriterion.entity.User;
import com.kriterion.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        authService = new AuthService(userRepository, passwordEncoder);
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

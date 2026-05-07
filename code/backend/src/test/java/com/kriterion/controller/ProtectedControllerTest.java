package com.kriterion.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests for route protection and authenticated access.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ProtectedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void protected_endpoint_without_token_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/protected/user-info"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protected_endpoint_with_invalid_token_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/protected/user-info")
                .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
    }

    // TODO: Add test with valid JWT token once helper methods for generating test JWT are available
    // @Test
    // void protected_endpoint_with_valid_token_shouldReturnUserInfo() throws Exception {
    //     String validToken = jwtTokenService.generateAccessToken("1");
    //     mockMvc.perform(get("/api/v1/protected/user-info")
    //             .header("Authorization", "Bearer " + validToken))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.success").value(true))
    //             .andExpect(jsonPath("$.data.userId").value("1"));
    // }
}

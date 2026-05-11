package com.kriterion.controller.auth;

import com.kriterion.dto.auth.RegisterRequest;
import com.kriterion.dto.auth.RegisterResponse;
import com.kriterion.dto.shared.ApiResponse;
import com.kriterion.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.kriterion.dto.shared.ApiResponse;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and token management.")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user", description = "Creates a new user account with email and password.")
    @PostMapping("/register")
    public ResponseEntity<com.kriterion.dto.shared.ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        Long userId = authService.register(request);
        RegisterResponse data = new RegisterResponse(userId);
        return ResponseEntity.ok(com.kriterion.dto.shared.ApiResponse.success("User registered successfully", data));
    }

    @Operation(summary = "User login", description = "Authenticates user and returns JWT access and refresh tokens.")
    @PostMapping("/login")
    public ResponseEntity<com.kriterion.dto.shared.ApiResponse<com.kriterion.dto.auth.AuthResponse>> login(@Valid @RequestBody com.kriterion.dto.auth.LoginRequest request) {
        com.kriterion.dto.auth.AuthResponse auth = authService.login(request);
        return ResponseEntity.ok(com.kriterion.dto.shared.ApiResponse.success("Login successful", auth));
    }

    @Operation(summary = "Refresh access token", description = "Uses a valid refresh token to obtain a new access token and a rotated refresh token.")
    @PostMapping("/refresh-token")
    public ResponseEntity<com.kriterion.dto.shared.ApiResponse<com.kriterion.dto.auth.TokenRefreshResponse>> refresh(@Valid @RequestBody com.kriterion.dto.auth.RefreshTokenRequest request) {
        com.kriterion.dto.auth.TokenRefreshResponse data = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(com.kriterion.dto.shared.ApiResponse.success("Token refreshed successfully", data));
    }

    @Operation(summary = "User logout", description = "Invalidates the provided refresh token.")
    @PostMapping("/logout")
    public ResponseEntity<com.kriterion.dto.shared.ApiResponse<Object>> logout(@Valid @RequestBody com.kriterion.dto.auth.RefreshTokenRequest request) {
        authService.revokeRefreshToken(request.getRefreshToken());
        return ResponseEntity.ok(com.kriterion.dto.shared.ApiResponse.success("Logged out successfully", null));
    }
}

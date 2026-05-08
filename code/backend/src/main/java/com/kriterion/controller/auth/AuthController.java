package com.kriterion.controller.auth;

import com.kriterion.dto.auth.RegisterRequest;
import com.kriterion.dto.auth.RegisterResponse;
import com.kriterion.response.ApiResponse;
import com.kriterion.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        Long userId = authService.register(request);
        RegisterResponse data = new RegisterResponse(userId);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", data));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<com.kriterion.dto.auth.AuthResponse>> login(@Valid @RequestBody com.kriterion.dto.auth.LoginRequest request) {
        com.kriterion.dto.auth.AuthResponse auth = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.success("Login successful", auth));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<java.util.Map<String, String>>> refresh(@Valid @RequestBody com.kriterion.dto.auth.RefreshTokenRequest request) {
        String newAccess = authService.refreshAccessToken(request.getRefreshToken());
        java.util.Map<String, String> data = java.util.Collections.singletonMap("accessToken", newAccess);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", data));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(@Valid @RequestBody com.kriterion.dto.auth.RefreshTokenRequest request) {
        authService.revokeRefreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }
}

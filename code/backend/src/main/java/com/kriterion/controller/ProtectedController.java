package com.kriterion.controller;

import com.kriterion.response.ApiResponse;
import com.kriterion.security.util.AuthenticationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Protected API endpoint example.
 * Requires authentication via JWT token.
 */
@RestController
@RequestMapping("/api/v1/protected")
@Slf4j
public class ProtectedController {

    @GetMapping("/user-info")
    public ResponseEntity<ApiResponse<Object>> getUserInfo() {
        String userId = AuthenticationUtil.getAuthenticatedUserId();
        log.info("User info requested by userId={}", userId);

        return ResponseEntity.ok(ApiResponse.success("User authenticated", 
            java.util.Collections.singletonMap("userId", userId)));
    }
}

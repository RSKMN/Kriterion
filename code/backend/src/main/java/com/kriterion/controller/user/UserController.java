package com.kriterion.controller.user;

import com.kriterion.dto.shared.ApiResponse;
import com.kriterion.security.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints for user profile and security.")
public class UserController {

    @Operation(summary = "Get current user", description = "Returns profile information for the currently authenticated user.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser() {
        String userId = AuthenticationUtil.getAuthenticatedUserId();
        log.info("User info requested for userId={}", userId);
        
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved", 
            Collections.singletonMap("userId", userId)));
    }
}

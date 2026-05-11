package com.kriterion.controller;

import com.kriterion.dto.shared.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "System", description = "Endpoints for service health and monitoring.")
public class HealthController {

    @Operation(summary = "Health check", description = "Verifies that the API service is responsive.")
    public ResponseEntity<ApiResponse<Object>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("Service is up and running", null));
    }
}

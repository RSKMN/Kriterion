package com.kriterion.controller;

import com.kriterion.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Object>> healthCheck() {
        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(true)
                .message("Service is up and running")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}

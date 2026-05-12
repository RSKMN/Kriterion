package com.kriterion.financialreflectionlite.controller;

import com.kriterion.financialreflectionlite.dto.FinancialReflectionResponse;
import com.kriterion.financialreflectionlite.service.FinancialReflectionLiteSeeder;
import com.kriterion.financialreflectionlite.service.FinancialReflectionServiceLite;
import com.kriterion.response.ApiResponse;
import com.kriterion.security.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/financial-reflection", "/reflection-lite"})
@RequiredArgsConstructor
public class FinancialReflectionLiteController {
    
    private final FinancialReflectionServiceLite reflectionService;
    private final FinancialReflectionLiteSeeder seeder;
    
    @GetMapping("/reflection")
    public ResponseEntity<ApiResponse<FinancialReflectionResponse>> getFinancialReflection() {
        FinancialReflectionResponse reflection = reflectionService.generateFinancialReflection();
        return ResponseEntity.ok(ApiResponse.success("Financial reflection analysis generated successfully", reflection));
    }

    @PostMapping("/seed")
    public ResponseEntity<ApiResponse<String>> seedDemoRhythms() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId != null) {
            seeder.seedDemoRhythms(userId);
            return ResponseEntity.ok(ApiResponse.success("Demo financial rhythms seeded successfully", null));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error("User not authenticated", null));
    }
}

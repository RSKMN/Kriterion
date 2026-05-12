package com.kriterion.behaviorlite.controller;

import com.kriterion.behaviorlite.dto.BehaviorLiteSummaryResponse;
import com.kriterion.behaviorlite.dto.BehaviorLiteResponseFactory;
import com.kriterion.behaviorlite.service.BehavioralInsightsLiteSeeder;
import com.kriterion.behaviorlite.service.BehavioralInsightsLiteService;
import com.kriterion.response.ApiResponse;
import com.kriterion.security.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/behavior-lite")
@RequiredArgsConstructor
public class BehaviorLiteController {

    private final BehavioralInsightsLiteService liteService;
    private final BehavioralInsightsLiteSeeder seeder;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<BehaviorLiteSummaryResponse>> getSummary() {
        BehaviorLiteSummaryResponse summary = liteService.buildSummary();
        return ResponseEntity.ok(ApiResponse.success("Behavioral lite summary retrieved successfully", summary));
    }

    @PostMapping("/seed")
    public ResponseEntity<ApiResponse<String>> seedDemoData() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId == null) {
            return ResponseEntity.ok(ApiResponse.success("No authenticated user context available", "not_seeded"));
        }

        boolean seeded = seeder.seedIfNeeded(userId);
        return ResponseEntity.ok(ApiResponse.success(seeded ? "Lite demo data seeded" : "Lite demo data already sufficient", seeded ? "seeded" : "skipped"));
    }
}
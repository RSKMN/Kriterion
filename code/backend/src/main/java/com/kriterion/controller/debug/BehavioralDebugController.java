package com.kriterion.controller.debug;

import com.kriterion.demo.BehavioralDemoSeeder;
import com.kriterion.response.ApiResponse;
import com.kriterion.security.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debug/seed")
@RequiredArgsConstructor
public class BehavioralDebugController {

    private final BehavioralDemoSeeder seeder;

    @PostMapping("/behavioral")
    public ResponseEntity<ApiResponse<String>> seedBehavioralData() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("User not authenticated", null));
        }
        
        seeder.seedBehavioralData(userId);
        return ResponseEntity.ok(ApiResponse.success("Behavioral demo data seeded successfully. " +
                "Please wait a few seconds for the background analysis to update.", null));
    }
}

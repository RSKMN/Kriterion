package com.kriterion.controller.analytics;

import com.kriterion.analytics.behavioral.CognitiveLoadEngine;
import com.kriterion.dto.analytics.behavioral.CognitiveLoadResponse;
import com.kriterion.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/cognitive-load")
@RequiredArgsConstructor
public class BehavioralCognitiveLoadController {

    private final CognitiveLoadEngine cognitiveLoadEngine;

    @GetMapping
    public ResponseEntity<ApiResponse<CognitiveLoadResponse>> getCognitiveLoad() {
        CognitiveLoadResponse load = cognitiveLoadEngine.estimateCognitiveLoad();
        return ResponseEntity.ok(ApiResponse.success("Financial cognitive load estimated successfully", load));
    }
}

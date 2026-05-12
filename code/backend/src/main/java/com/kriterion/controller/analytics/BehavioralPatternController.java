package com.kriterion.controller.analytics;

import com.kriterion.analytics.behavioral.BehavioralPatternEngine;
import com.kriterion.dto.analytics.behavioral.BehavioralPatternResponse;
import com.kriterion.entity.BehavioralPattern;
import com.kriterion.response.ApiResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/patterns")
@RequiredArgsConstructor
public class BehavioralPatternController {

    private final com.kriterion.analytics.behavioral.PatternDetectionService patternDetectionService;
    private final BehavioralPatternEngine patternEngine;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BehavioralPatternResponse>>> getPatterns() {
        List<BehavioralPattern> patterns = patternDetectionService.getPatternsForUser();
        List<BehavioralPatternResponse> response = patterns.stream()
                .map(p -> BehavioralPatternResponse.builder()
                        .id(p.getId())
                        .patternName(p.getPatternName())
                        .confidenceScore(p.getConfidenceScore())
                        .detectedAt(p.getDetectedAt())
                        .lastObservedAt(p.getLastObservedAt())
                        .evidenceMetadata(p.getEvidenceMetadata())
                        .isActive(p.getIsActive())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Patterns retrieved successfully", response));
    }

    @PostMapping("/analyze")
    public ResponseEntity<ApiResponse<List<BehavioralPatternResponse>>> runAnalysis() {
        List<BehavioralPattern> patterns = patternEngine.runAnalysis();
        List<BehavioralPatternResponse> response = patterns.stream()
                .map(p -> BehavioralPatternResponse.builder()
                        .id(p.getId())
                        .patternName(p.getPatternName())
                        .confidenceScore(p.getConfidenceScore())
                        .detectedAt(p.getDetectedAt())
                        .lastObservedAt(p.getLastObservedAt())
                        .evidenceMetadata(p.getEvidenceMetadata())
                        .isActive(p.getIsActive())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Behavioral analysis completed successfully", response));
    }
}

package com.kriterion.financialreflectionlite.service;

import com.kriterion.financialreflectionlite.dto.*;
import com.kriterion.security.util.AuthenticationUtil;
import com.kriterion.entity.Transaction;
import com.kriterion.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialReflectionServiceLite {
    
    private final TransactionRepository transactionRepository;
    private final SpendingRhythmAnalyzerLite rhythmAnalyzer;
    private final TransactionDensityAnalyzerLite densityAnalyzer;
    private final FinancialStructureAnalyzerLite structureAnalyzer;
    private final BehavioralStabilityServiceLite stabilityAnalyzer;
    private final ReflectionTemplateEngineLite reflectionEngine;
    
    public FinancialReflectionResponse generateFinancialReflection() {
        try {
            Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
            if (userId == null) {
                return FinancialReflectionResponse.empty();
            }
            
            List<Transaction> transactions = transactionRepository.findByUserId(userId);
            
            if (transactions == null || transactions.size() < 5) {
                return buildEmptyReflectionResponse();
            }
            
            // Run all analyses
            SpendingRhythmAnalysis rhythmAnalysis = rhythmAnalyzer.analyzeSpendingRhythm(transactions);
            TransactionDensityAnalysis densityAnalysis = densityAnalyzer.analyzeTransactionDensity(transactions);
            FinancialStructureAnalysis structureAnalysis = structureAnalyzer.analyzeFinancialStructure(transactions);
            BehavioralStabilityAnalysis stabilityAnalysis = stabilityAnalyzer.analyzeBehavioralStability(transactions);
            
            // Generate reflections from all analyses
            List<ReflectionResponse> reflections = reflectionEngine.generateReflections(
                    rhythmAnalysis, densityAnalysis, structureAnalysis, stabilityAnalysis
            );
            
            // Calculate overall metrics
            Double overallConfidence = (rhythmAnalysis.getConfidence() + 
                                       densityAnalysis.getConfidence() + 
                                       structureAnalysis.getConfidence() + 
                                       stabilityAnalysis.getConfidence()) / 4.0;
            
            String dataStatus = transactions.size() < 20 ? "partial" : "ready";
            String overallStability = stabilityAnalysis.getStabilityLevel();
            
            return FinancialReflectionResponse.builder()
                    .spendingRhythm(rhythmAnalysis)
                    .transactionDensity(densityAnalysis)
                    .financialStructure(structureAnalysis)
                    .behavioralStability(stabilityAnalysis)
                    .reflections(reflections)
                    .dataStatus(dataStatus)
                    .overallStability(overallStability)
                    .analysisConfidence(overallConfidence)
                    .generatedAt(LocalDateTime.now())
                    .fallback(false)
                    .build();
                    
        } catch (Exception e) {
            return FinancialReflectionResponse.empty();
        }
    }
    
    private FinancialReflectionResponse buildEmptyReflectionResponse() {
        return FinancialReflectionResponse.builder()
                .spendingRhythm(SpendingRhythmAnalysis.builder()
                        .rhythmPattern("unknown")
                        .confidence(0.0)
                        .temporalObservation("Financial reflections improve as more transaction rhythms become available.")
                        .build())
                .transactionDensity(TransactionDensityAnalysis.builder()
                        .densityLevel("unknown")
                        .confidence(0.0)
                        .densityObservation("Not enough temporal financial history yet to generate reflective insights.")
                        .build())
                .financialStructure(FinancialStructureAnalysis.builder()
                        .structureStability("unknown")
                        .confidence(0.0)
                        .subscriptionPressure(SubscriptionPressureAnalysis.builder()
                                .pressureLevel("unknown")
                                .pressureScore(0.0)
                                .build())
                        .build())
                .behavioralStability(BehavioralStabilityAnalysis.builder()
                        .stabilityLevel("unknown")
                        .confidence(0.0)
                        .build())
                .reflections(List.of())
                .dataStatus("insufficient_data")
                .overallStability("unknown")
                .analysisConfidence(0.0)
                .generatedAt(LocalDateTime.now())
                .fallback(true)
                .build();
    }
}

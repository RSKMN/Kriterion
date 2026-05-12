package com.kriterion.financialreflectionlite.service;

import com.kriterion.ai.llm.OllamaClient;
import com.kriterion.financialreflectionlite.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReflectionTemplateEngineLite {
    
    private final OllamaClient ollamaClient;
    
    public List<ReflectionResponse> generateReflections(
            SpendingRhythmAnalysis rhythmAnalysis,
            TransactionDensityAnalysis densityAnalysis,
            FinancialStructureAnalysis structureAnalysis,
            BehavioralStabilityAnalysis stabilityAnalysis) {
        
        List<ReflectionResponse> reflections = new ArrayList<>();
        
        // Spending Rhythm Reflections
        if (rhythmAnalysis.getConfidence() > 0.3) {
            if (rhythmAnalysis.getHasNightSpendingPattern()) {
                reflections.add(ReflectionResponse.builder()
                        .category("rhythm")
                        .title("Late-Night Spending Pattern")
                        .observation("Your spending rhythm becomes noticeably active during late-night hours. This temporal pattern may suggest reactive or discretionary purchase behavior.")
                        .tone("analytical")
                        .weight(0.7)
                        .isSignificant(true)
                        .build());
            }
            
            if ("bursty".equals(rhythmAnalysis.getRhythmPattern())) {
                reflections.add(ReflectionResponse.builder()
                        .category("rhythm")
                        .title("Irregular Transaction Bursts")
                        .observation("Your spending rhythm shows irregular clustering rather than steady patterns. Transactions tend to occur in concentrated bursts rather than distributed throughout time periods.")
                        .tone("analytical")
                        .weight(0.65)
                        .isSignificant(true)
                        .build());
            } else if ("structured".equals(rhythmAnalysis.getRhythmPattern())) {
                reflections.add(ReflectionResponse.builder()
                        .category("rhythm")
                        .title("Structured Spending Rhythm")
                        .observation("Your spending rhythm exhibits structured regularity. Transactions occur at predictable intervals, suggesting consistent financial habits.")
                        .tone("positive")
                        .weight(0.6)
                        .isSignificant(false)
                        .build());
            }

            // User example: "Your spending rhythm became less structured during periods of elevated transaction activity."
            if (densityAnalysis.getHasBurstPatterns() && !"structured".equals(rhythmAnalysis.getRhythmPattern())) {
                reflections.add(ReflectionResponse.builder()
                        .category("rhythm")
                        .title("Rhythm Variance")
                        .observation("Your spending rhythm became less structured during periods of elevated transaction activity.")
                        .tone("analytical")
                        .weight(0.75)
                        .isSignificant(true)
                        .build());
            }
        }
        
        // Transaction Density Reflections
        if (densityAnalysis.getConfidence() > 0.3) {
            if (densityAnalysis.getHasBurstPatterns()) {
                reflections.add(ReflectionResponse.builder()
                        .category("density")
                        .title("Clustering in Transaction Activity")
                        .observation("Transaction activity shows clustering patterns. Days with multiple transactions are interspersed with days of minimal activity.")
                        .tone("analytical")
                        .weight(0.6)
                        .isSignificant(true)
                        .build());
            }
            
            if ("very_high".equals(densityAnalysis.getDensityLevel())) {
                reflections.add(ReflectionResponse.builder()
                        .category("density")
                        .title("High Transaction Frequency")
                        .observation("Transaction frequency is notably elevated. This may reflect frequent, smaller purchases rather than consolidated spending episodes.")
                        .tone("cautionary")
                        .weight(0.65)
                        .isSignificant(true)
                        .build());
            }
        }
        
        // Financial Structure Reflections
        if (structureAnalysis.getConfidence() > 0.3) {
            if (structureAnalysis.getRecurringRatio() > 0.6) {
                reflections.add(ReflectionResponse.builder()
                        .category("structure")
                        .title("Elevated Recurring Obligations")
                        .observation("Recurring obligations comprise a substantial portion of total spending. This structural pattern may reduce spending flexibility.")
                        .tone("analytical")
                        .weight(0.7)
                        .isSignificant(true)
                        .build());
            }
            
            // User example: "Recurring obligations appear to reduce spending stability during later monthly cycles."
            if (structureAnalysis.getRecurringRatio() > 0.4 && "volatile".equals(stabilityAnalysis.getStabilityLevel())) {
                reflections.add(ReflectionResponse.builder()
                        .category("structure")
                        .title("Structural Stability Pressure")
                        .observation("Recurring obligations appear to reduce spending stability during later monthly cycles.")
                        .tone("analytical")
                        .weight(0.8)
                        .isSignificant(true)
                        .build());
            }

            // User example: "Your spending consistency improves when recurring obligations are handled earlier in the month."
            if (structureAnalysis.getRecurringRatio() > 0.3 && "stable".equals(stabilityAnalysis.getStabilityLevel())) {
                reflections.add(ReflectionResponse.builder()
                        .category("structure")
                        .title("Consistent Structural Handling")
                        .observation("Your spending consistency improves when recurring obligations are handled earlier in the month.")
                        .tone("positive")
                        .weight(0.7)
                        .isSignificant(false)
                        .build());
            }
        }

        // Behavioral Stability Reflections
        if (stabilityAnalysis.getConfidence() > 0.3) {
            if ("volatile".equals(stabilityAnalysis.getStabilityLevel())) {
                reflections.add(ReflectionResponse.builder()
                        .category("stability")
                        .title("Behavioral Rhythm Variance")
                        .observation("Observable variability in transaction timing and volume suggests a less structured period.")
                        .tone("analytical")
                        .weight(0.7)
                        .isSignificant(true)
                        .build());
            }

            // User example: "Discretionary purchases increased during irregular transaction periods."
            if (structureAnalysis.getDiscretionaryRatio() > 0.6 && "irregular".equals(rhythmAnalysis.getRhythmPattern())) {
                reflections.add(ReflectionResponse.builder()
                        .category("stability")
                        .title("Discretionary Pattern Shift")
                        .observation("Discretionary purchases increased during irregular transaction periods.")
                        .tone("analytical")
                        .weight(0.7)
                        .isSignificant(true)
                        .build());
            }
        }
        
        // Optionally refine wording with Qwen
        refineWithQwen(reflections);
        
        return reflections;
    }
    
    private void refineWithQwen(List<ReflectionResponse> reflections) {
        if (reflections.isEmpty()) return;
        
        try {
            for (ReflectionResponse reflection : reflections) {
                String prompt = String.format(
                        "Rewrite the following financial behavioral reflection to be more analytical, research-oriented, and concise. " +
                        "Maintain a non-invasive, probabilistic tone. Avoid emotional language, therapy-speak, or emojis. " +
                        "Observation: \"%s\"", 
                        reflection.getObservation()
                );
                
                String refined = ollamaClient.generate(prompt);
                if (refined != null && !refined.isBlank() && refined.length() < 300) {
                    // Basic cleanup if Qwen returns extra text
                    refined = refined.replaceAll("^\"|\"$", "").trim();
                    reflection.setObservation(refined);
                }
            }
        } catch (Exception e) {
            log.warn("Qwen wording refinement failed, falling back to templates: {}", e.getMessage());
        }
    }
}

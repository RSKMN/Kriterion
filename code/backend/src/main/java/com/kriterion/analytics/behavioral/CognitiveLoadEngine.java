package com.kriterion.analytics.behavioral;

import com.kriterion.analytics.behavioral.util.CognitiveLoadCalculator;
import com.kriterion.analytics.behavioral.util.FinancialPressureAnalyzer;
import com.kriterion.dto.analytics.behavioral.CognitiveLoadResponse;
import com.kriterion.dto.analytics.behavioral.LoadFactor;
import com.kriterion.analytics.behavioral.util.BehavioralResponseFactory;
import com.kriterion.entity.BehavioralMetrics;
import com.kriterion.entity.Transaction;
import com.kriterion.repository.BehavioralMetricsRepository;
import com.kriterion.repository.TransactionRepository;
import com.kriterion.security.util.AuthenticationUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CognitiveLoadEngine {

    private final BehavioralMetricsRepository metricsRepository;
    private final TransactionRepository transactionRepository;
    private final FinancialPressureAnalyzer pressureAnalyzer;
    private final CognitiveLoadCalculator calculator;

    public CognitiveLoadResponse estimateCognitiveLoad() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId == null) return BehavioralResponseFactory.createDefaultCognitiveLoad();

        log.info("Estimating financial cognitive load for user: {}", userId);

        Optional<BehavioralMetrics> metricsOpt = metricsRepository.findByUserId(userId);
        List<Transaction> transactions = transactionRepository.findByUserId(userId);

        if (metricsOpt.isEmpty() || transactions.isEmpty()) {
            return BehavioralResponseFactory.createDefaultCognitiveLoad();
        }

        BehavioralMetrics metrics = metricsOpt.get();
        double budgetPressure = pressureAnalyzer.calculateUnresolvedBudgetPressure(userId);
        double subscriptionLoad = pressureAnalyzer.calculateSubscriptionLoad(transactions);
        double microLoad = pressureAnalyzer.calculateMicroTransactionLoad(transactions);

        double volatilityScore = calculator.calculateVolatilityScore(metrics);
        double rhythmScore = calculator.calculateRhythmStabilityScore(metrics);
        double structureScore = calculator.calculateSpendingStructureScore(metrics);
        double pressureScore = calculator.calculateObligationPressureScore(metrics, subscriptionLoad, budgetPressure);

        double aggregateLoad = calculator.aggregateCognitiveLoad(
            volatilityScore, 
            metrics.getCategoryInstability() != null ? metrics.getCategoryInstability() / 4.0 : 0.0, 
            pressureScore, 
            (1.0 - structureScore)
        );

        List<LoadFactor> factors = calculator.identifyTopFactors(metrics, budgetPressure, subscriptionLoad);
        List<String> interpretations = generateInterpretations(volatilityScore, rhythmScore, structureScore, pressureScore);

        return CognitiveLoadResponse.builder()
            .cognitiveLoadScore(Math.min(1.0, aggregateLoad))
            .volatilityScore(volatilityScore)
            .rhythmStabilityScore(rhythmScore)
            .spendingStructureScore(structureScore)
            .obligationPressureScore(pressureScore)
            .factors(factors)
            .interpretations(interpretations)
            .status("STABLE")
            .build();
    }

    private List<String> generateInterpretations(double volatility, double rhythm, double structure, double pressure) {
        List<String> results = new ArrayList<>();
        
        if (volatility > 0.7) results.add("Elevated decision volatility detected");
        if (rhythm < 0.3) results.add("Reduced spending consistency");
        if (structure < 0.4) results.add("Fragmented spending rhythm");
        if (pressure > 0.6) results.add("Recurring obligation pressure increase");
        
        if (results.isEmpty()) {
            results.add("Stable financial behavioral load");
        }
        
        return results;
    }
}

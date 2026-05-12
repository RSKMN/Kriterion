package com.kriterion.analytics.behavioral;

import com.kriterion.analytics.behavioral.util.BehavioralResponseFactory;
import com.kriterion.dto.analytics.behavioral.PredictionResponse;
import com.kriterion.entity.BehavioralMetrics;
import com.kriterion.entity.BehavioralPattern;
import com.kriterion.repository.BehavioralMetricsRepository;
import com.kriterion.repository.BehavioralPatternRepository;
import com.kriterion.repository.TransactionRepository;
import com.kriterion.security.util.AuthenticationUtil;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private final BehavioralMetricsRepository metricsRepository;
    private final BehavioralPatternRepository patternRepository;
    private final TransactionRepository transactionRepository;

    public PredictionResponse getPrediction() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId == null) {
            return BehavioralResponseFactory.createDefaultPrediction();
        }

        Optional<BehavioralMetrics> metricsOpt = metricsRepository.findByUserId(userId);
        List<BehavioralPattern> patterns = patternRepository.findByUserId(userId);
        List<?> transactions = transactionRepository.findByUserId(userId);

        if (metricsOpt.isEmpty() || transactions.isEmpty()) {
            return BehavioralResponseFactory.createDefaultPrediction();
        }

        BehavioralMetrics metrics = metricsOpt.get();
        double volatility = safe(metrics.getSpendingVolatility());
        double discretionary = safe(metrics.getDiscretionarySpendingRatio());
        double recurrence = safe(metrics.getRecurringObligationPressure());
        double rhythm = safe(metrics.getTransactionRhythmConsistency());
        double burst = safe(metrics.getSpendingBurstFrequency());
        double fragmentation = safe(metrics.getAverageTransactionFragmentation());

        double confidence = Math.min(1.0, 0.15 + (transactions.size() / 120.0) + (patterns.size() / 10.0));
        String risk = volatility > 1.2 || discretionary > 0.45 || burst > 0.25 ? "high" : recurrence > 0.4 || fragmentation > 2.0 ? "moderate" : "low";
        String trend = rhythm > 0.55 ? "stable" : discretionary > 0.35 ? "upward" : "mixed";

        List<String> signals = List.of(
                "volatility=" + round(volatility),
                "discretionary_ratio=" + round(discretionary),
                "recurring_pressure=" + round(recurrence),
                "burst_frequency=" + round(burst),
                "pattern_count=" + patterns.size()
        );

        return PredictionResponse.builder()
                .overspendingRisk(risk)
                .trendDirection(trend)
                .confidenceScore(confidence)
                .confidenceState(confidence < 0.35 ? "insufficient_data" : confidence < 0.65 ? "low_confidence" : "high_confidence")
                .forecastNarrative(confidence < 0.35
                        ? "Forecast confidence remains limited until more recurring financial behavior is observed."
                        : "The current pattern set suggests " + risk + " overspending risk with a " + trend + " spending trajectory.")
                .supportingSignals(signals)
                .status(confidence < 0.35 ? "LOW_CONFIDENCE" : "STABLE")
                .isFallback(confidence < 0.35)
                .build();
    }

    private double safe(Double value) {
        return value == null ? 0.0 : value;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
package com.kriterion.predictivelite.service;

import com.kriterion.predictivelite.dto.PredictiveForecastResponse;
import com.kriterion.predictivelite.dto.PredictiveResponseFactory;
import com.kriterion.entity.Transaction;
import com.kriterion.repository.TransactionRepository;
import com.kriterion.security.util.AuthenticationUtil;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PredictiveAnalyticsLiteService {

    private final TransactionRepository transactionRepository;
    private final OverspendingPredictorLite overspendingPredictor;
    private final SavingsForecastEngineLite savingsForecast;
    private final VolatilityTrendAnalyzerLite volatilityAnalyzer;
    private final SubscriptionCreepDetectorLite subscriptionCreep;
    private final EndOfMonthRiskAnalyzerLite endOfMonthRisk;
    private final PredictiveReflectionServiceLite reflectionService;

    public PredictiveForecastResponse buildForecast() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId == null) {
            return PredictiveResponseFactory.defaultForecast();
        }

        try {
            List<Transaction> transactions = transactionRepository.findByUserId(userId);
            if (transactions == null || transactions.isEmpty()) {
                return PredictiveResponseFactory.defaultForecast();
            }

            List<Transaction> ordered = transactions.stream()
                    .sorted(Comparator.comparing(Transaction::getTransactionDate)
                            .thenComparing(transaction -> transaction.getTransactionTime() == null ? java.time.LocalTime.MIDNIGHT : transaction.getTransactionTime()))
                    .toList();

            // Run all predictors
            OverspendingPredictorLite.OverspendingForecast overspending = overspendingPredictor.predictOverspending(ordered);
            SavingsForecastEngineLite.SavingsForecast savings = savingsForecast.forecastSavings(ordered);
            VolatilityTrendAnalyzerLite.VolatilityForecast volatility = volatilityAnalyzer.analyzeVolatilityTrend(ordered);
            SubscriptionCreepDetectorLite.SubscriptionCreepForecast subscription = subscriptionCreep.detectSubscriptionCreep(ordered);
            EndOfMonthRiskAnalyzerLite.EndOfMonthRisk endOfMonth = endOfMonthRisk.analyzeEndOfMonthRisk(ordered);

            // Build forecast response
            PredictiveForecastResponse response = PredictiveForecastResponse.builder()
                    .overspendingRisk(overspending.risk())
                    .forecastDaysRemaining(overspending.daysRemaining())
                    .volatilityTrend(volatility.trend())
                    .subscriptionPressureTrend(subscription.trend())
                    .savingsTrend(savings.trend())
                    .confidence(Math.max(
                            Math.max(overspending.confidence(), savings.confidence()),
                            Math.max(volatility.confidence(), subscription.confidence())
                    ))
                    .dataStatus(ordered.size() < 10 ? "insufficient_data" : ordered.size() < 30 ? "partial" : "ready")
                    .generatedAt(java.time.LocalDateTime.now())
                    .fallback(ordered.size() < 10)
                    .build();

            // Add insights
            var insights = reflectionService.buildReflections(response, overspending, volatility, subscription, savings, endOfMonth);
            response.setInsights(insights);

            return response;
        } catch (Exception exception) {
            log.error("Predictive forecast generation failed", exception);
            return PredictiveResponseFactory.defaultForecast();
        }
    }
}

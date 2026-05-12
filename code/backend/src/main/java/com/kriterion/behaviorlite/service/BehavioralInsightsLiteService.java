package com.kriterion.behaviorlite.service;

import com.kriterion.behaviorlite.dto.BehaviorLiteResponseFactory;
import com.kriterion.behaviorlite.dto.BehaviorLiteSummaryResponse;
import com.kriterion.behaviorlite.dto.BurstWindowResponse;
import com.kriterion.behaviorlite.dto.LiteReflectionResponse;
import com.kriterion.behaviorlite.dto.SeriesPointResponse;
import com.kriterion.behaviorlite.dto.PredictLiteForecastResponse;
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
public class BehavioralInsightsLiteService {

    private final TransactionRepository transactionRepository;
    private final VolatilityCalculatorLite volatilityCalculatorLite;
    private final FinancialRhythmAnalyzerLite financialRhythmAnalyzerLite;
    private final SubscriptionPressureServiceLite subscriptionPressureServiceLite;
    private final SpendingBurstDetectorLite spendingBurstDetectorLite;
    private final SavingsTrendAnalyzerLite savingsTrendAnalyzerLite;
    private final ReflectionTemplateServiceLite reflectionTemplateServiceLite;

    public BehaviorLiteSummaryResponse buildSummary() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId == null) {
            return BehaviorLiteResponseFactory.defaultSummary();
        }

        try {
            List<Transaction> transactions = transactionRepository.findByUserId(userId);
            if (transactions == null || transactions.isEmpty()) {
                return BehaviorLiteResponseFactory.defaultSummary();
            }

            List<Transaction> ordered = transactions.stream()
                    .sorted(Comparator.comparing(Transaction::getTransactionDate)
                            .thenComparing(transaction -> transaction.getTransactionTime() == null ? java.time.LocalTime.MIDNIGHT : transaction.getTransactionTime()))
                    .toList();

            double volatility = volatilityCalculatorLite.calculateWeeklySpendingVolatility(ordered);
            var weeklySeries = volatilityCalculatorLite.buildWeeklySpendingSeries(ordered);
            var hourlyDistribution = financialRhythmAnalyzerLite.buildHourlyDistribution(ordered);
            double lateNightRatio = financialRhythmAnalyzerLite.calculateLateNightRatio(ordered);
            double subscriptionPressure = subscriptionPressureServiceLite.calculateSubscriptionPressure(ordered);
            List<BurstWindowResponse> bursts = spendingBurstDetectorLite.detectBursts(ordered);
            SavingsTrendAnalyzerLite.SavingsTrendResult savingsTrendResult = savingsTrendAnalyzerLite.analyzeTrend(ordered);

            int incomeCount = (int) ordered.stream().filter(transaction -> transaction.getType() == com.kriterion.entity.enums.TransactionType.INCOME).count();
            int expenseCount = (int) ordered.stream().filter(transaction -> transaction.getType() == com.kriterion.entity.enums.TransactionType.EXPENSE).count();

                List<LiteReflectionResponse> reflections = reflectionTemplateServiceLite.buildReflections(BehaviorLiteSummaryResponse.builder()
                    .volatility(volatility)
                    .lateNightRatio(lateNightRatio)
                    .subscriptionPressure(subscriptionPressure)
                    .savingsTrend(savingsTrendResult.slope())
                    .savingsTrendLabel(savingsTrendResult.label())
                    .hourlyDistribution(hourlyDistribution)
                    .weeklySpending(weeklySeries)
                    .savingsSeries(savingsTrendResult.series())
                    .bursts(bursts)
                    .insights(List.of())
                    .transactionCount(ordered.size())
                    .incomeCount(incomeCount)
                    .expenseCount(expenseCount)
                    .dataStatus(ordered.size() < 6 ? "insufficient_data" : ordered.size() < 20 ? "partial" : "ready")
                    .generatedAt(java.time.LocalDateTime.now())
                    .fallback(ordered.size() < 6)
                    .build());

                return BehaviorLiteSummaryResponse.builder()
                    .volatility(volatility)
                    .lateNightRatio(lateNightRatio)
                    .subscriptionPressure(subscriptionPressure)
                    .savingsTrend(savingsTrendResult.slope())
                    .savingsTrendLabel(savingsTrendResult.label())
                    .hourlyDistribution(hourlyDistribution)
                    .weeklySpending(weeklySeries)
                    .savingsSeries(savingsTrendResult.series())
                    .bursts(bursts)
                    .insights(List.of())
                    .transactionCount(ordered.size())
                    .incomeCount(incomeCount)
                    .expenseCount(expenseCount)
                    .dataStatus(ordered.size() < 6 ? "insufficient_data" : ordered.size() < 20 ? "partial" : "ready")
                    .generatedAt(java.time.LocalDateTime.now())
                    .fallback(ordered.size() < 6)
                    .insights(reflections)
                    .build();
        } catch (Exception exception) {
            log.error("Lite behavioral summary failed", exception);
            return BehaviorLiteResponseFactory.defaultSummary();
        }
    }

    public PredictLiteForecastResponse buildForecast(BehaviorLiteSummaryResponse summary) {
        if (summary == null) {
            return BehaviorLiteResponseFactory.defaultForecast();
        }

        try {
            double riskScore = 0.0;
            riskScore += Math.min(1.0, summary.getVolatility() == null ? 0.0 : summary.getVolatility() / 2.0) * 0.4;
            riskScore += Math.min(1.0, summary.getSubscriptionPressure() == null ? 0.0 : summary.getSubscriptionPressure()) * 0.35;
            riskScore += Math.min(1.0, summary.getLateNightRatio() == null ? 0.0 : summary.getLateNightRatio()) * 0.15;
            riskScore += summary.getBursts() == null ? 0.0 : Math.min(1.0, summary.getBursts().size() / 4.0) * 0.1;

            String riskLevel = riskScore > 0.65 ? "high" : riskScore > 0.35 ? "moderate" : "low";
            String trendDirection = summary.getSavingsTrend() == null || summary.getSavingsTrend() >= 0 ? "stable" : "downward";
            double confidence = summary.getDataStatus() != null && "ready".equals(summary.getDataStatus())
                    ? 0.8
                    : summary.getDataStatus() != null && "partial".equals(summary.getDataStatus()) ? 0.55 : 0.25;

            List<String> signals = List.of(
                    "volatility=" + format(summary.getVolatility()),
                    "late_night_ratio=" + format(summary.getLateNightRatio()),
                    "subscription_pressure=" + format(summary.getSubscriptionPressure()),
                    "burst_windows=" + (summary.getBursts() == null ? 0 : summary.getBursts().size()),
                    "savings_trend=" + format(summary.getSavingsTrend())
            );

            String narrative;
            if (summary.getFallback() != null && summary.getFallback()) {
                narrative = "Forecasting is intentionally conservative until more transaction history is available.";
            } else if ("high".equals(riskLevel)) {
                narrative = "Recurring obligations and recent burst spending suggest a higher short-term overspending risk.";
            } else if ("moderate".equals(riskLevel)) {
                narrative = "Current activity shows some pressure, but the financial rhythm remains readable.";
            } else {
                narrative = "The lite model currently sees a relatively stable pattern with manageable spending pressure.";
            }

            return PredictLiteForecastResponse.builder()
                    .riskLevel(riskLevel)
                    .trendDirection(trendDirection)
                    .confidence(confidence)
                    .narrative(narrative)
                    .signals(signals)
                    .dataStatus(summary.getDataStatus())
                    .fallback(Boolean.TRUE.equals(summary.getFallback()) || confidence < 0.4)
                    .build();
        } catch (Exception exception) {
            log.error("Lite forecast failed", exception);
            return BehaviorLiteResponseFactory.defaultForecast();
        }
    }

    private String format(Double value) {
        return String.valueOf(Math.round((value == null ? 0.0 : value) * 100.0) / 100.0);
    }
}
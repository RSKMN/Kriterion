package com.kriterion.analytics.behavioral;

import com.kriterion.analytics.behavioral.util.FinancialRhythmAnalyzer;
import com.kriterion.analytics.behavioral.util.SpendingPatternUtilities;
import com.kriterion.analytics.behavioral.util.TransactionDensityAnalyzer;
import com.kriterion.analytics.behavioral.util.VolatilityCalculator;
import com.kriterion.analytics.behavioral.util.BehavioralResponseFactory;
import com.kriterion.dto.analytics.BehavioralMetricsResponse;
import com.kriterion.entity.BehavioralMetrics;
import com.kriterion.entity.Transaction;
import com.kriterion.entity.enums.TransactionType;
import com.kriterion.exception.UnauthorizedException;
import com.kriterion.repository.BehavioralMetricsRepository;
import com.kriterion.repository.TransactionRepository;
import com.kriterion.security.util.AuthenticationUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BehavioralMetricsService {

    private final BehavioralMetricsRepository metricsRepository;
    private final TransactionRepository transactionRepository;
    private final com.kriterion.repository.UserRepository userRepository;
    private final FinancialRhythmAnalyzer rhythmAnalyzer;
    private final VolatilityCalculator volatilityCalculator;
    private final TransactionDensityAnalyzer densityAnalyzer;
    private final SpendingPatternUtilities patternUtils;

    @Transactional
    public BehavioralMetricsResponse getBehavioralMetrics() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId == null) throw new UnauthorizedException("User not authenticated");

                try {
                        List<Transaction> transactions = transactionRepository.findByUserId(userId);
                        if (transactions.isEmpty()) {
                                return BehavioralResponseFactory.createDefaultMetrics();
                        }

                        BehavioralMetrics metrics = metricsRepository.findByUserId(userId)
                                        .orElseGet(() -> {
                                                com.kriterion.entity.User user = userRepository.findById(userId)
                                                                .orElseThrow(() -> new UnauthorizedException("User not found"));
                                                return BehavioralMetrics.builder().user(user).build();
                                        });

                        computeAndSetMetrics(metrics, transactions);
                        metrics.setLastComputedAt(LocalDateTime.now());
                        metricsRepository.save(metrics);

                        return mapToResponse(metrics, transactions);
                } catch (Exception exception) {
                        log.error("Behavioral metrics calculation failed for user {}", userId, exception);
                        return BehavioralResponseFactory.createDefaultMetrics();
                }
    }

    private void computeAndSetMetrics(BehavioralMetrics metrics, List<Transaction> transactions) {
        List<Transaction> expenses = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.toList());

        // 1. Transaction Frequency
        long daysInRange = calculateDaysInRange(transactions);
        metrics.setTransactionFrequency(daysInRange > 0 ? (double) transactions.size() / daysInRange : transactions.size());

        // 2. Decision Density
        metrics.setDecisionDensity(densityAnalyzer.calculateDecisionDensity(transactions));

        // 3. Spending Volatility
        List<BigDecimal> amounts = expenses.stream().map(Transaction::getAmount).collect(Collectors.toList());
        metrics.setSpendingVolatility(volatilityCalculator.calculateCoefficientOfVariation(amounts));

        // 4. Category Instability
        List<String> categories = expenses.stream()
                .map(Transaction::getCategory)
                .filter(category -> category != null && category.getName() != null)
                .map(category -> category.getName())
                .collect(Collectors.toList());
        metrics.setCategoryInstability(patternUtils.calculateEntropy(categories));

        // 5. Discretionary Spending Ratio
        BigDecimal totalExpense = expenses.stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discretionaryExpense = expenses.stream()
                .filter(t -> patternUtils.isDiscretionary(t.getCategory()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        metrics.setDiscretionarySpendingRatio(totalExpense.signum() > 0 ? discretionaryExpense.doubleValue() / totalExpense.doubleValue() : 0.0);

        // 6. Recurring Obligation Pressure
        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal recurringExpenses = expenses.stream()
                .filter(t -> t.getIsRecurring() != null && t.getIsRecurring())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        metrics.setRecurringObligationPressure(totalIncome.signum() > 0 ? recurringExpenses.doubleValue() / totalIncome.doubleValue() : 0.0);

        // 7. Late Night Spending Ratio
        metrics.setLateNightSpendingRatio(rhythmAnalyzer.calculateLateNightSpendingRatio(expenses));

        // 8. Spending Burst Frequency
        metrics.setSpendingBurstFrequency(densityAnalyzer.calculateBurstFrequency(expenses));

        // 9. Average Transaction Fragmentation
        metrics.setAverageTransactionFragmentation(densityAnalyzer.calculateFragmentation(expenses));

        // 10. Transaction Rhythm Consistency
        metrics.setTransactionRhythmConsistency(rhythmAnalyzer.calculateRhythmConsistency(transactions));
    }

    private BehavioralMetricsResponse mapToResponse(BehavioralMetrics metrics, List<Transaction> transactions) {
        BehavioralMetricsResponse response = BehavioralMetricsResponse.builder()
                .transactionFrequency(metrics.getTransactionFrequency())
                .decisionDensity(metrics.getDecisionDensity())
                .spendingVolatility(metrics.getSpendingVolatility())
                .categoryInstability(metrics.getCategoryInstability())
                .discretionarySpendingRatio(metrics.getDiscretionarySpendingRatio())
                .recurringObligationPressure(metrics.getRecurringObligationPressure())
                .lateNightSpendingRatio(metrics.getLateNightSpendingRatio())
                .spendingBurstFrequency(metrics.getSpendingBurstFrequency())
                .averageTransactionFragmentation(metrics.getAverageTransactionFragmentation())
                .transactionRhythmConsistency(metrics.getTransactionRhythmConsistency())
                .lastComputedAt(metrics.getLastComputedAt())
                .build();

        // Temporal Analysis
        response.setHourlyPatterns(rhythmAnalyzer.getHourlyPatterns(transactions));
        response.setDailyPatterns(getDailyPatterns(transactions));
        response.setWeeklyPatterns(getWeeklyPatterns(transactions));
        response.setMonthlyPatterns(getMonthlyPatterns(transactions));
        response.setPostSalarySpendingShift(calculatePostSalaryShift(transactions));

        return response;
    }

    private long calculateDaysInRange(List<Transaction> transactions) {
        if (transactions.isEmpty()) return 0;
        LocalDate min = transactions.stream().map(Transaction::getTransactionDate).min(Comparator.naturalOrder()).get();
        LocalDate max = transactions.stream().map(Transaction::getTransactionDate).max(Comparator.naturalOrder()).get();
        return ChronoUnit.DAYS.between(min, max) + 1;
    }

    private Map<String, Double> getDailyPatterns(List<Transaction> transactions) {
        Map<String, Long> counts = transactions.stream()
                .collect(Collectors.groupingBy(t -> t.getTransactionDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH), Collectors.counting()));
        long total = transactions.size();
                if (total == 0) return Map.of();
                return counts.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() / (double) total));
    }

    private Map<Integer, Double> getWeeklyPatterns(List<Transaction> transactions) {
        // Week of month
        Map<Integer, Long> counts = transactions.stream()
                .collect(Collectors.groupingBy(t -> (t.getTransactionDate().getDayOfMonth() - 1) / 7 + 1, Collectors.counting()));
        long total = transactions.size();
                if (total == 0) return Map.of();
                return counts.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() / (double) total));
    }

    private Map<Integer, Double> getMonthlyPatterns(List<Transaction> transactions) {
        Map<Integer, Long> counts = transactions.stream()
                .collect(Collectors.groupingBy(t -> t.getTransactionDate().getMonthValue(), Collectors.counting()));
        long total = transactions.size();
        return counts.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() / (double) total));
    }

    private double calculatePostSalaryShift(List<Transaction> transactions) {
        // Comparison of spending in first week of month vs last week of month
        List<Transaction> expenses = transactions.stream().filter(t -> t.getType() == TransactionType.EXPENSE).collect(Collectors.toList());
        if (expenses.isEmpty()) return 0.0;

        double firstWeekAvg = expenses.stream()
                .filter(t -> t.getTransactionDate().getDayOfMonth() <= 7)
                .mapToDouble(t -> t.getAmount().doubleValue())
                .average()
                .orElse(0.0);

        double lastWeekAvg = expenses.stream()
                .filter(t -> t.getTransactionDate().getDayOfMonth() >= 23)
                .mapToDouble(t -> t.getAmount().doubleValue())
                .average()
                .orElse(0.0);

        if (lastWeekAvg == 0) return firstWeekAvg > 0 ? 1.0 : 0.0;
        return (firstWeekAvg - lastWeekAvg) / lastWeekAvg;
    }
}

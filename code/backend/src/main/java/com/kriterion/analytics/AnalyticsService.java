package com.kriterion.analytics;

import com.kriterion.dto.analytics.CategoryBreakdownResponse;
import com.kriterion.dto.analytics.CategorySpendingResponse;
import com.kriterion.dto.analytics.DashboardSummaryResponse;
import com.kriterion.dto.analytics.MonthlySummaryResponse;
import com.kriterion.dto.analytics.MonthlyTrendsResponse;
import com.kriterion.dto.analytics.TopCategoriesResponse;
import com.kriterion.dto.analytics.WeeklyInsightResponse;
import com.kriterion.dto.analytics.WeeklyInsightsResponse;
import com.kriterion.exception.UnauthorizedException;
import com.kriterion.repository.AnalyticsRepository;
import com.kriterion.repository.projection.CategorySpendingProjection;
import com.kriterion.repository.projection.DailyInsightProjection;
import com.kriterion.repository.projection.MonthlyTrendProjection;
import com.kriterion.repository.projection.OverallTotalsProjection;
import com.kriterion.security.util.AuthenticationUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary() {
        Long userId = requireAuthenticatedUserId();
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusMonths(11).withDayOfMonth(1);

        OverallTotalsProjection totals = analyticsRepository.findOverallTotals(userId);
        BigDecimal totalIncome = normalize(totals != null ? totals.getTotalIncome() : null);
        BigDecimal totalExpense = normalize(totals != null ? totals.getTotalExpense() : null);
        BigDecimal totalBalance = totalIncome.subtract(totalExpense);

        List<MonthlySummaryResponse> monthlySummary = buildMonthlySummary(userId, startDate, today);
        YearMonth currentMonth = YearMonth.from(today);
        BigDecimal monthlyExpense = monthlySummary.stream()
                .filter(summary -> currentMonth.toString().equals(summary.getMonthKey()))
                .map(MonthlySummaryResponse::getTotalExpense)
                .findFirst()
                .orElse(BigDecimal.ZERO);

        return DashboardSummaryResponse.builder()
                .totalBalance(totalBalance)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .monthlyExpense(monthlyExpense)
                .savings(totalBalance)
                .remainingBalance(totalBalance)
                .monthlySummary(monthlySummary)
                .build();
    }

    @Transactional(readOnly = true)
    public MonthlyTrendsResponse getMonthlyTrends() {
        Long userId = requireAuthenticatedUserId();
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusMonths(11).withDayOfMonth(1);

        return MonthlyTrendsResponse.builder()
                .startDate(startDate)
                .endDate(today)
                .trends(buildMonthlySummary(userId, startDate, today))
                .build();
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<MonthlySummaryResponse> getMonthlyHistory(org.springframework.data.domain.Pageable pageable) {
        Long userId = requireAuthenticatedUserId();
        // For history, we might want to go back further than 12 months.
        // Let's assume we want to show all available months.
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusYears(10); // Far enough back

        List<MonthlySummaryResponse> allTrends = buildMonthlySummary(userId, startDate, endDate);
        // Sort descending by month
        allTrends.sort((a, b) -> b.getMonthKey().compareTo(a.getMonthKey()));

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allTrends.size());
        
        List<MonthlySummaryResponse> content = (start < allTrends.size()) 
                ? allTrends.subList(start, end) 
                : new ArrayList<>();
                
        return new org.springframework.data.domain.PageImpl<>(content, pageable, allTrends.size());
    }

    @Transactional(readOnly = true)
    public CategoryBreakdownResponse getCategoryBreakdown() {
        Long userId = requireAuthenticatedUserId();
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.withDayOfMonth(1);

        List<CategorySpendingResponse> categories = buildCategorySpending(userId, startDate, today);
        BigDecimal totalExpense = categories.stream()
                .map(CategorySpendingResponse::getTotalSpent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CategoryBreakdownResponse.builder()
                .startDate(startDate)
                .endDate(today)
                .totalExpense(totalExpense)
                .categories(categories)
                .build();
    }

    @Transactional(readOnly = true)
    public TopCategoriesResponse getTopCategories() {
        Long userId = requireAuthenticatedUserId();
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.withDayOfMonth(1);

        List<CategorySpendingResponse> categories = buildCategorySpending(userId, startDate, today)
                .stream()
                .sorted(Comparator.comparing(CategorySpendingResponse::getTotalSpent).reversed())
                .limit(5)
                .toList();

        return TopCategoriesResponse.builder()
                .startDate(startDate)
                .endDate(today)
                .categories(categories)
                .build();
    }

    @Transactional(readOnly = true)
    public WeeklyInsightsResponse getWeeklyInsights() {
        Long userId = requireAuthenticatedUserId();
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        List<WeeklyInsightResponse> insights = buildWeeklyInsights(userId, weekStart, today);
        BigDecimal totalIncome = insights.stream().map(WeeklyInsightResponse::getTotalIncome).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpense = insights.stream().map(WeeklyInsightResponse::getTotalExpense).reduce(BigDecimal.ZERO, BigDecimal::add);

        return WeeklyInsightsResponse.builder()
                .weekStart(weekStart)
                .weekEnd(today)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(totalIncome.subtract(totalExpense))
                .insights(insights)
                .build();
    }

    private List<MonthlySummaryResponse> buildMonthlySummary(Long userId, LocalDate startDate, LocalDate endDate) {
        Map<YearMonth, MonthlyTrendProjection> trendMap = analyticsRepository.findMonthlyTrends(userId, startDate, endDate)
                .stream()
                .collect(Collectors.toMap(
                        projection -> YearMonth.of(projection.getYear(), projection.getMonth()),
                        Function.identity(),
                        (left, right) -> left,
                        LinkedHashMap::new));

        List<MonthlySummaryResponse> summaries = new ArrayList<>();
        YearMonth cursor = YearMonth.from(startDate);
        YearMonth end = YearMonth.from(endDate);

        while (!cursor.isAfter(end)) {
            MonthlyTrendProjection projection = trendMap.get(cursor);
            BigDecimal totalIncome = normalize(projection != null ? projection.getTotalIncome() : null);
            BigDecimal totalExpense = normalize(projection != null ? projection.getTotalExpense() : null);

            summaries.add(MonthlySummaryResponse.builder()
                    .monthKey(cursor.toString())
                    .monthLabel(cursor.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + cursor.getYear())
                    .totalIncome(totalIncome)
                    .totalExpense(totalExpense)
                    .balance(totalIncome.subtract(totalExpense))
                    .build());

            cursor = cursor.plusMonths(1);
        }

        return summaries;
    }

    private List<CategorySpendingResponse> buildCategorySpending(Long userId, LocalDate startDate, LocalDate endDate) {
        List<CategorySpendingProjection> projections = analyticsRepository.findCategorySpendingBreakdown(userId, startDate, endDate);
        BigDecimal totalExpense = projections.stream()
                .map(CategorySpendingProjection::getTotalSpent)
                .filter(value -> value != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return projections.stream()
                .map(projection -> {
                    BigDecimal totalSpent = normalize(projection.getTotalSpent());
                    BigDecimal percentage = totalExpense.signum() == 0
                            ? BigDecimal.ZERO
                            : totalSpent.multiply(BigDecimal.valueOf(100)).divide(totalExpense, 2, RoundingMode.HALF_UP);

                    return CategorySpendingResponse.builder()
                            .categoryId(projection.getCategoryId())
                            .categoryName(projection.getCategoryName())
                            .categoryColor(projection.getCategoryColor())
                            .totalSpent(totalSpent)
                            .transactionCount(normalizeLong(projection.getTransactionCount()))
                            .percentage(percentage)
                            .build();
                })
                .toList();
    }

    private List<WeeklyInsightResponse> buildWeeklyInsights(Long userId, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, DailyInsightProjection> insightMap = analyticsRepository.findWeeklyInsights(userId, startDate, endDate)
                .stream()
                .collect(Collectors.toMap(DailyInsightProjection::getInsightDate, Function.identity(), (left, right) -> left, LinkedHashMap::new));

        List<WeeklyInsightResponse> insights = new ArrayList<>();
        LocalDate cursor = startDate;

        while (!cursor.isAfter(endDate)) {
            DailyInsightProjection projection = insightMap.get(cursor);
            BigDecimal totalIncome = normalize(projection != null ? projection.getTotalIncome() : null);
            BigDecimal totalExpense = normalize(projection != null ? projection.getTotalExpense() : null);

            insights.add(WeeklyInsightResponse.builder()
                    .date(cursor)
                    .dayLabel(cursor.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                    .totalIncome(totalIncome)
                    .totalExpense(totalExpense)
                    .balance(totalIncome.subtract(totalExpense))
                    .build());

            cursor = cursor.plusDays(1);
        }

        return insights;
    }

    private Long requireAuthenticatedUserId() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        return userId;
    }

    private BigDecimal normalize(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Long normalizeLong(Long value) {
        return value == null ? 0L : value;
    }
}

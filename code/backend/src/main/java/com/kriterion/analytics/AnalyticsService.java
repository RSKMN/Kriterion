package com.kriterion.analytics;

import com.kriterion.dto.analytics.DashboardSummaryResponse;
import com.kriterion.dto.analytics.MonthlySummaryResponse;
import com.kriterion.exception.UnauthorizedException;
import com.kriterion.repository.TransactionRepository;
import com.kriterion.repository.projection.DashboardBalanceProjection;
import com.kriterion.repository.projection.MonthlyBalanceProjection;
import com.kriterion.security.util.AuthenticationUtil;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        DashboardBalanceProjection balance = transactionRepository.findDashboardBalanceByUserId(userId);
        BigDecimal totalIncome = normalize(balance != null ? balance.getTotalIncome() : null);
        BigDecimal totalExpense = normalize(balance != null ? balance.getTotalExpense() : null);

        List<MonthlySummaryResponse> monthlySummary = transactionRepository.findMonthlyBalanceByUserId(userId)
                .stream()
                .map(this::mapMonthlySummary)
                .toList();

        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .remainingBalance(totalIncome.subtract(totalExpense))
                .monthlySummary(monthlySummary)
                .build();
    }

    private MonthlySummaryResponse mapMonthlySummary(MonthlyBalanceProjection projection) {
        BigDecimal totalIncome = normalize(projection.getTotalIncome());
        BigDecimal totalExpense = normalize(projection.getTotalExpense());

        return MonthlySummaryResponse.builder()
                .month(YearMonth.of(projection.getYear(), projection.getMonth()).toString())
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .remainingBalance(totalIncome.subtract(totalExpense))
                .build();
    }

    private BigDecimal normalize(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}

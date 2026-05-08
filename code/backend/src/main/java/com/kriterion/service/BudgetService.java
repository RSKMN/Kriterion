package com.kriterion.service;

import com.kriterion.dto.budget.BudgetRequest;
import com.kriterion.dto.budget.BudgetResponse;
import com.kriterion.dto.budget.BudgetStatusResponse;
import com.kriterion.entity.Budget;
import com.kriterion.entity.Category;
import com.kriterion.exception.ApiException;
import com.kriterion.repository.BudgetRepository;
import com.kriterion.repository.CategoryRepository;
import com.kriterion.repository.TransactionRepository;
import com.kriterion.security.util.AuthenticationUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public BudgetResponse createBudget(BudgetRequest request) {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        
        // Check for existing budget for same category/month/year
        Optional<Budget> existing = budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
                userId, request.getCategoryId(), request.getMonth(), request.getYear());
        
        if (existing.isPresent()) {
            throw new ApiException("Budget already exists for this category and period", HttpStatus.CONFLICT);
        }

        Budget budget = new Budget();
        budget.setUserId(userId);
        budget.setCategoryId(request.getCategoryId());
        budget.setMonthlyLimit(request.getMonthlyLimit());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        budget.setAlertThreshold(80); // Default threshold
        budget.setCurrentSpent(BigDecimal.ZERO);

        Budget saved = budgetRepository.save(budget);
        return mapToResponse(saved);
    }

    @Transactional
    public BudgetResponse updateBudget(Long id, BudgetRequest request) {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ApiException("Budget not found", HttpStatus.NOT_FOUND));

        if (!budget.getUserId().equals(userId)) {
            throw new ApiException("Unauthorized to update this budget", HttpStatus.FORBIDDEN);
        }

        budget.setMonthlyLimit(request.getMonthlyLimit());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        // Category change is typically not allowed for an existing budget record, 
        // but if needed, we'd add validation here.

        Budget updated = budgetRepository.save(budget);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteBudget(Long id) {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ApiException("Budget not found", HttpStatus.NOT_FOUND));

        if (!budget.getUserId().equals(userId)) {
            throw new ApiException("Unauthorized to delete this budget", HttpStatus.FORBIDDEN);
        }

        budgetRepository.delete(budget);
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> getBudgets(int month, int year) {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        return budgetRepository.findByUserIdAndMonthAndYear(userId, month, year)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BudgetStatusResponse getBudgetStatus(int month, int year) {
        List<BudgetResponse> budgets = getBudgets(month, year);
        
        BigDecimal totalLimit = budgets.stream()
                .map(BudgetResponse::getMonthlyLimit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalSpent = budgets.stream()
                .map(BudgetResponse::getCurrentSpent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRemaining = totalLimit.subtract(totalSpent);
        BigDecimal totalPercentage = totalLimit.compareTo(BigDecimal.ZERO) > 0
                ? totalSpent.multiply(new BigDecimal(100)).divide(totalLimit, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long warnings = budgets.stream().filter(BudgetResponse::isWarning).count();
        long exceeded = budgets.stream().filter(BudgetResponse::isExceeded).count();

        return BudgetStatusResponse.builder()
                .totalBudgetLimit(totalLimit)
                .totalSpent(totalSpent)
                .totalRemaining(totalRemaining)
                .totalPercentage(totalPercentage)
                .budgetsCount(budgets.size())
                .warningBudgetsCount((int) warnings)
                .exceededBudgetsCount((int) exceeded)
                .budgets(budgets)
                .build();
    }

    private BudgetResponse mapToResponse(Budget budget) {
        BigDecimal spent = transactionRepository.calculateSpentAmount(
                budget.getUserId(), budget.getCategoryId(), budget.getMonth(), budget.getYear());
        
        BigDecimal limit = budget.getMonthlyLimit();
        BigDecimal remaining = limit.subtract(spent);
        BigDecimal percentage = limit.compareTo(BigDecimal.ZERO) > 0
                ? spent.multiply(new BigDecimal(100)).divide(limit, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        int threshold = budget.getAlertThreshold() != null ? budget.getAlertThreshold() : 80;
        boolean isWarning = percentage.compareTo(new BigDecimal(threshold)) >= 0 && percentage.compareTo(new BigDecimal(100)) < 0;
        boolean isExceeded = percentage.compareTo(new BigDecimal(100)) >= 0;

        String categoryName = "Overall";
        if (budget.getCategoryId() != null) {
            categoryName = categoryRepository.findById(budget.getCategoryId())
                    .map(Category::getName)
                    .orElse("Unknown Category");
        }

        return BudgetResponse.builder()
                .id(budget.getId())
                .categoryId(budget.getCategoryId())
                .categoryName(categoryName)
                .monthlyLimit(limit)
                .currentSpent(spent)
                .remaining(remaining)
                .percentage(percentage)
                .month(budget.getMonth())
                .year(budget.getYear())
                .alertThreshold(threshold)
                .isWarning(isWarning)
                .isExceeded(isExceeded)
                .build();
    }
}

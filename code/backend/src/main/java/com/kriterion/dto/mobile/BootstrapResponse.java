package com.kriterion.dto.mobile;

import com.kriterion.dto.user.UserResponse;
import com.kriterion.dto.category.CategoryResponse;
import com.kriterion.dto.budget.BudgetResponse;
import com.kriterion.dto.recurring.RecurringTransactionResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BootstrapResponse {
    private UserResponse profile;
    private List<CategoryResponse> categories;
    private List<BudgetResponse> budgets;
    private List<RecurringTransactionResponse> recurringTransactions;
    private LocalDateTime serverTime;
    private Map<String, Object> analyticsEssentials;
    private String syncToken;
}

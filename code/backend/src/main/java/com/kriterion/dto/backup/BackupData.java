package com.kriterion.dto.backup;

import com.kriterion.dto.budget.BudgetResponse;
import com.kriterion.dto.recurring.RecurringTransactionResponse;
import com.kriterion.dto.transaction.TransactionResponse;
import com.kriterion.dto.category.CategoryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackupData {
    private List<CategoryResponse> categories;
    private List<TransactionResponse> transactions;
    private List<BudgetResponse> budgets;
    private List<RecurringTransactionResponse> recurringTransactions;
    private String version;
    private String exportDate;
}

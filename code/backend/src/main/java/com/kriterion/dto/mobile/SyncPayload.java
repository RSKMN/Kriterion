package com.kriterion.dto.mobile;

import com.kriterion.dto.transaction.TransactionResponse;
import com.kriterion.dto.category.CategoryResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SyncPayload {
    private LocalDateTime syncTime;
    private List<TransactionResponse> transactions;
    private List<CategoryResponse> categories;
    private List<Long> deletedTransactionIds;
    private List<Long> deletedCategoryIds;
}

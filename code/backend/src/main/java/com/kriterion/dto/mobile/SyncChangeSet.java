package com.kriterion.dto.mobile;

import com.kriterion.dto.transaction.CreateTransactionRequest;
import com.kriterion.dto.transaction.UpdateTransactionRequest;
import com.kriterion.dto.category.CategoryResponse;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncChangeSet {
    private List<CreateTransactionRequest> newTransactions;
    private List<UpdateTransactionRequest> updatedTransactions;
    private List<Long> deletedTransactionIds;
    
    // Support for category changes from mobile if allowed
    private List<CategoryResponse> newCategories;
}

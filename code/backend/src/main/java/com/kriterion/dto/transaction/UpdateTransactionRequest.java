package com.kriterion.dto.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateTransactionRequest extends TransactionRequest {
    // inherits all fields from TransactionRequest
}

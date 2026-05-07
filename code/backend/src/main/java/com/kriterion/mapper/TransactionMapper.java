package com.kriterion.mapper;

import com.kriterion.dto.transaction.TransactionRequest;
import com.kriterion.entity.Transaction;

public interface TransactionMapper {
    Transaction toEntity(TransactionRequest request);
}

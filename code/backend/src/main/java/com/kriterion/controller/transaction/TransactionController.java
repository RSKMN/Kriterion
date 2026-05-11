package com.kriterion.controller.transaction;

import com.kriterion.dto.transaction.CreateTransactionRequest;
import com.kriterion.dto.transaction.TransactionResponse;
import com.kriterion.dto.transaction.UpdateTransactionRequest;
import com.kriterion.dto.shared.ApiResponse;
import com.kriterion.dto.shared.PagedResponse;
import com.kriterion.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Endpoints for managing financial transactions.")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Get transactions", description = "Retrieves a paginated list of transactions with optional filtering.")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<TransactionResponse>>> getTransactions(
            @Parameter(description = "Filter by category ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Filter by transaction type") @RequestParam(required = false) com.kriterion.entity.enums.TransactionType type,
            @Parameter(description = "Filter by start date (YYYY-MM-DD)") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "Filter by end date (YYYY-MM-DD)") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "Search by title or description") @RequestParam(required = false) String search,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort property") @RequestParam(defaultValue = "transactionDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TransactionResponse> transactions = transactionService.getTransactions(categoryId, type, startDate, endDate, search, pageable);
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", PagedResponse.of(transactions)));
    }

    @Operation(summary = "Create transaction", description = "Creates a new transaction for the authenticated user.")
    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request) {
        TransactionResponse transaction = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transaction created successfully", transaction));
    }

    @Operation(summary = "Update transaction", description = "Updates an existing transaction by ID.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> updateTransaction(
            @Parameter(description = "Transaction ID") @PathVariable Long id,
            @Valid @RequestBody UpdateTransactionRequest request) {
        TransactionResponse transaction = transactionService.updateTransaction(id, request);
        return ResponseEntity.ok(ApiResponse.success("Transaction updated successfully", transaction));
    }

    @Operation(summary = "Delete transaction", description = "Deletes a transaction by ID (soft-delete for mobile sync).")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteTransaction(@Parameter(description = "Transaction ID") @PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok(ApiResponse.success("Transaction deleted successfully", null));
    }
}

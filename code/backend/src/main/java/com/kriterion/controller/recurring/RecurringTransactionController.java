package com.kriterion.controller.recurring;

import com.kriterion.dto.recurring.RecurringTransactionRequest;
import com.kriterion.dto.recurring.RecurringTransactionResponse;
import com.kriterion.dto.shared.ApiResponse;
import com.kriterion.service.RecurringTransactionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/transactions/recurring")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Endpoints for managing recurring financial patterns.")
public class RecurringTransactionController {

    private final RecurringTransactionService recurringTransactionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RecurringTransactionResponse>>> getAll() {
        List<RecurringTransactionResponse> response = recurringTransactionService.getAllRecurringTransactions();
        return ResponseEntity.ok(ApiResponse.success("Recurring transactions retrieved", response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RecurringTransactionResponse>> create(@Valid @RequestBody RecurringTransactionRequest request) {
        RecurringTransactionResponse response = recurringTransactionService.createRecurringTransaction(request);
        return ResponseEntity.ok(ApiResponse.success("Recurring transaction created", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RecurringTransactionResponse>> update(
            @PathVariable Long id, 
            @Valid @RequestBody RecurringTransactionRequest request) {
        RecurringTransactionResponse response = recurringTransactionService.updateRecurringTransaction(id, request);
        return ResponseEntity.ok(ApiResponse.success("Recurring transaction updated", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        recurringTransactionService.deleteRecurringTransaction(id);
        return ResponseEntity.ok(ApiResponse.success("Recurring transaction deleted", null));
    }
}

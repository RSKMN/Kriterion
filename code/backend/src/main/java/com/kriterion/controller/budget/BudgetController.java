package com.kriterion.controller.budget;

import com.kriterion.dto.budget.BudgetRequest;
import com.kriterion.dto.budget.BudgetResponse;
import com.kriterion.dto.budget.BudgetStatusResponse;
import com.kriterion.dto.shared.ApiResponse;
import com.kriterion.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/budgets")
@RequiredArgsConstructor
@Tag(name = "Budgets", description = "Endpoints for managing monthly budgets and tracking spending limits.")
public class BudgetController {

    private final BudgetService budgetService;

    @Operation(summary = "Create budget", description = "Sets a new monthly budget limit for a category.")
    @PostMapping
    public ResponseEntity<ApiResponse<BudgetResponse>> createBudget(@Valid @RequestBody BudgetRequest request) {
        BudgetResponse response = budgetService.createBudget(request);
        return ResponseEntity.ok(ApiResponse.success("Budget created successfully", response));
    }

    @Operation(summary = "Update budget", description = "Updates an existing budget limit.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetResponse>> updateBudget(
            @Parameter(description = "Budget ID") @PathVariable Long id, 
            @Valid @RequestBody BudgetRequest request) {
        BudgetResponse response = budgetService.updateBudget(id, request);
        return ResponseEntity.ok(ApiResponse.success("Budget updated successfully", response));
    }

    @Operation(summary = "Delete budget", description = "Removes a budget limit.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(@Parameter(description = "Budget ID") @PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.ok(ApiResponse.success("Budget deleted successfully", null));
    }

    @Operation(summary = "Get budgets", description = "Retrieves all budgets for a given month/year.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getBudgets(
            @Parameter(description = "Month (1-12)") @RequestParam(required = false) Integer month,
            @Parameter(description = "Year (e.g. 2026)") @RequestParam(required = false) Integer year) {
        
        LocalDate now = LocalDate.now();
        int m = (month != null) ? month : now.getMonthValue();
        int y = (year != null) ? year : now.getYear();
        
        List<BudgetResponse> response = budgetService.getBudgets(m, y);
        return ResponseEntity.ok(ApiResponse.success("Budgets retrieved successfully", response));
    }

    @Operation(summary = "Get budget status", description = "Returns a high-level overview of budget usage (total budget vs total spent).")
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<BudgetStatusResponse>> getBudgetStatus(
            @Parameter(description = "Month (1-12)") @RequestParam(required = false) Integer month,
            @Parameter(description = "Year (e.g. 2026)") @RequestParam(required = false) Integer year) {
        
        LocalDate now = LocalDate.now();
        int m = (month != null) ? month : now.getMonthValue();
        int y = (year != null) ? year : now.getYear();
        
        BudgetStatusResponse response = budgetService.getBudgetStatus(m, y);
        return ResponseEntity.ok(ApiResponse.success("Budget status retrieved successfully", response));
    }
}

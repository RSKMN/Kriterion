package com.kriterion.controller.budget;

import com.kriterion.dto.budget.BudgetRequest;
import com.kriterion.dto.budget.BudgetResponse;
import com.kriterion.dto.budget.BudgetStatusResponse;
import com.kriterion.response.ApiResponse;
import com.kriterion.service.BudgetService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<ApiResponse<BudgetResponse>> createBudget(@Valid @RequestBody BudgetRequest request) {
        BudgetResponse response = budgetService.createBudget(request);
        return ResponseEntity.ok(ApiResponse.success("Budget created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetResponse>> updateBudget(
            @PathVariable Long id, 
            @Valid @RequestBody BudgetRequest request) {
        BudgetResponse response = budgetService.updateBudget(id, request);
        return ResponseEntity.ok(ApiResponse.success("Budget updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.ok(ApiResponse.success("Budget deleted successfully", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getBudgets(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        
        LocalDate now = LocalDate.now();
        int m = (month != null) ? month : now.getMonthValue();
        int y = (year != null) ? year : now.getYear();
        
        List<BudgetResponse> response = budgetService.getBudgets(m, y);
        return ResponseEntity.ok(ApiResponse.success("Budgets retrieved successfully", response));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<BudgetStatusResponse>> getBudgetStatus(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        
        LocalDate now = LocalDate.now();
        int m = (month != null) ? month : now.getMonthValue();
        int y = (year != null) ? year : now.getYear();
        
        BudgetStatusResponse response = budgetService.getBudgetStatus(m, y);
        return ResponseEntity.ok(ApiResponse.success("Budget status retrieved successfully", response));
    }
}

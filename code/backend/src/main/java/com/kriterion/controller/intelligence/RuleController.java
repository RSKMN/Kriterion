package com.kriterion.controller.intelligence;

import com.kriterion.dto.shared.ApiResponse;
import com.kriterion.entity.CategorizationRule;
import com.kriterion.repository.CategorizationRuleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/intelligence/rules")
@RequiredArgsConstructor
@Tag(name = "System", description = "Endpoints for managing intelligent rule-based systems.")
public class RuleController {

    private final CategorizationRuleRepository ruleRepository;

    @Operation(summary = "Get categorization rules", description = "Retrieves all active categorization rules for the system.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategorizationRule>>> getRules() {
        return ResponseEntity.ok(ApiResponse.success("Rules retrieved successfully", ruleRepository.findAll()));
    }

    @Operation(summary = "Create categorization rule", description = "Adds a new rule to the engine (Admin only).")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategorizationRule>> createRule(@RequestBody CategorizationRule rule) {
        CategorizationRule saved = ruleRepository.save(rule);
        return ResponseEntity.ok(ApiResponse.success("Rule created successfully", saved));
    }

    @Operation(summary = "Delete categorization rule", description = "Removes a rule from the engine (Admin only).")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRule(@PathVariable Long id) {
        ruleRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Rule deleted successfully", null));
    }
}

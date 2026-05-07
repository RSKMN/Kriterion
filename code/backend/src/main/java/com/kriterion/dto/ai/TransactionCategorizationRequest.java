package com.kriterion.dto.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TransactionCategorizationRequest {
    @NotBlank
    private String merchantName;

    private String description;
}

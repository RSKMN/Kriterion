package com.kriterion.dto.category;

import com.kriterion.entity.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    private TransactionType type;

    @Size(max = 20)
    private String color;
}

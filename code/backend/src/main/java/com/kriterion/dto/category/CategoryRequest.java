package com.kriterion.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    @NotBlank(message = "Category name is required")
    @jakarta.validation.constraints.Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
    private String name;
    
    @jakarta.validation.constraints.NotNull(message = "Category type is required")
    private com.kriterion.entity.enums.CategoryType type;

    @jakarta.validation.constraints.Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Invalid color format")
    private String color;

    @jakarta.validation.constraints.Size(max = 50, message = "Icon name cannot exceed 50 characters")
    private String icon;
}

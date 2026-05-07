package com.kriterion.dto.category;

import com.kriterion.entity.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name cannot exceed 100 characters")
    private String name;

    @NotNull(message = "Category type is required")
    private CategoryType type;

    @Size(max = 100, message = "Icon length cannot exceed 100 characters")
    private String icon;

    @Size(max = 20, message = "Color length cannot exceed 20 characters")
    private String color;
}

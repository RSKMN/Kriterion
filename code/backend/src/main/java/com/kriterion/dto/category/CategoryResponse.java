package com.kriterion.dto.category;

import com.kriterion.entity.enums.CategoryType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private CategoryType type;
    private String icon;
    private String color;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private Long version;
}

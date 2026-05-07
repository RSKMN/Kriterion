package com.kriterion.dto.analytics;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopCategoriesResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<CategorySpendingResponse> categories;
}
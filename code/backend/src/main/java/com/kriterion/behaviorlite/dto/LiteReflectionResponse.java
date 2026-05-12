package com.kriterion.behaviorlite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiteReflectionResponse {
    private String title;
    private String description;
    private String emphasis;
}
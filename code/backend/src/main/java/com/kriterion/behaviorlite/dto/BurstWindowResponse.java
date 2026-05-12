package com.kriterion.behaviorlite.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BurstWindowResponse {
    private String label;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer transactionCount;
    private Double totalAmount;
}
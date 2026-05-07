package com.kriterion.dto.ocr;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OcrScanResponse {
    private String merchant;
    private BigDecimal amount;
    private LocalDate date;
    private BigDecimal confidence;
}

package com.kriterion.dto.ocr;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptUploadResponse {
    private String fileId;
    private String fileName;
    private String contentType;
    private long size;
    private String checksum;
    private String uploadStatus;
    private LocalDateTime uploadedAt;
}

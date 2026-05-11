package com.kriterion.entity;

import com.kriterion.entity.enums.OcrStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "receipts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receipt extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "file_id", nullable = false, unique = true)
    private String fileId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "storage_path")
    private String storagePath;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "checksum")
    private String checksum;

    @Enumerated(EnumType.STRING)
    @Column(name = "ocr_status")
    private OcrStatus ocrStatus = OcrStatus.PENDING;

    @Column(name = "raw_ocr_data", columnDefinition = "TEXT")
    private String rawOcrData;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}

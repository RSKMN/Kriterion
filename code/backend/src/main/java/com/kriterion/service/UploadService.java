package com.kriterion.service;

import com.kriterion.dto.ocr.ReceiptUploadResponse;
import com.kriterion.entity.Receipt;
import com.kriterion.entity.User;
import com.kriterion.entity.enums.OcrStatus;
import com.kriterion.exception.BadRequestException;
import com.kriterion.repository.ReceiptRepository;
import com.kriterion.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UploadService {

    private final UserRepository userRepository;
    private final ReceiptRepository receiptRepository;

    @Value("${app.upload.dir:uploads/receipts}")
    private String uploadDir;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/webp", "application/pdf"
    );

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            log.error("Could not initialize upload directory: {}", e.getMessage());
        }
    }

    public ReceiptUploadResponse uploadReceipt(MultipartFile file, Long userId) {
        validateFile(file);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        String fileId = UUID.randomUUID().toString();
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String storedFileName = fileId + extension;
        Path targetLocation = Paths.get(uploadDir).resolve(storedFileName);

        try {
            Files.copy(file.getInputStream(), targetLocation);
            
            String checksum = DigestUtils.md5DigestAsHex(Files.newInputStream(targetLocation));

            // Save metadata to DB
            Receipt receipt = Receipt.builder()
                    .user(user)
                    .fileId(fileId)
                    .fileName(originalFilename)
                    .storagePath(targetLocation.toString())
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .checksum(checksum)
                    .ocrStatus(OcrStatus.PENDING)
                    .build();
            
            receiptRepository.save(receipt);

            log.info("User {} uploaded receipt: {} -> {} (Size: {})", userId, originalFilename, storedFileName, file.getSize());

            return ReceiptUploadResponse.builder()
                    .fileId(fileId)
                    .fileName(originalFilename)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .checksum(checksum)
                    .uploadStatus("SUCCESS")
                    .uploadedAt(LocalDateTime.now())
                    .build();

        } catch (IOException e) {
            log.error("Failed to store file {}: {}", originalFilename, e.getMessage());
            throw new RuntimeException("Could not store file. Please try again!", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("Failed to store empty file.");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Invalid file type. Only JPEG, PNG, WEBP, and PDF are allowed.");
        }

        // Additional integrity checks can be added here
    }

    public List<ReceiptUploadResponse> getUploadHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));
        
        return receiptRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ReceiptUploadResponse mapToResponse(Receipt receipt) {
        return ReceiptUploadResponse.builder()
                .fileId(receipt.getFileId())
                .fileName(receipt.getFileName())
                .contentType(receipt.getContentType())
                .size(receipt.getFileSize())
                .checksum(receipt.getChecksum())
                .uploadStatus("SUCCESS")
                .uploadedAt(receipt.getCreatedAt())
                .build();
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}

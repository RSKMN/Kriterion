package com.kriterion.controller.ocr;

import com.kriterion.dto.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ocr")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Endpoints for AI-powered transaction entry and receipt scanning.")
public class OcrController {
    
    private final com.kriterion.service.UploadService uploadService;

    @Operation(summary = "Scan receipt", description = "Uploads a receipt image and initiates the OCR process. Supports JPEG, PNG, WEBP.")
    @PostMapping(value = "/scan-receipt", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<com.kriterion.dto.ocr.ReceiptUploadResponse>> scanReceipt(
            @RequestParam("file") MultipartFile file) {
        
        Long userId = com.kriterion.security.util.AuthenticationUtil.getAuthenticatedUserIdAsLong();
        com.kriterion.dto.ocr.ReceiptUploadResponse response = uploadService.uploadReceipt(file, userId);
        
        return ResponseEntity.ok(ApiResponse.success("Receipt uploaded and queued for processing", response));
    }

    @Operation(summary = "Get scan history", description = "Retrieves history of scanned receipts for the current user.")
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<java.util.List<com.kriterion.dto.ocr.ReceiptUploadResponse>>> getScanHistory() {
        Long userId = com.kriterion.security.util.AuthenticationUtil.getAuthenticatedUserIdAsLong();
        java.util.List<com.kriterion.dto.ocr.ReceiptUploadResponse> history = uploadService.getUploadHistory(userId);
        return ResponseEntity.ok(ApiResponse.success("Scan history retrieved", history));
    }
}

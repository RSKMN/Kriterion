package com.kriterion.controller.backup;

import com.kriterion.entity.BackupMetadata;
import com.kriterion.dto.shared.ApiResponse;
import com.kriterion.service.BackupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/backups")
@RequiredArgsConstructor
@Tag(name = "System", description = "Endpoints for service health, monitoring, and data backups.")
public class BackupController {

    private final BackupService backupService;

    @PostMapping("/generate")
    @Operation(summary = "Generate a full data backup")
    public ResponseEntity<byte[]> generateBackup() {
        byte[] backupData = backupService.generateBackup();
        String fileName = "kriterion_backup_" + LocalDate.now() + ".json";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(backupData);
    }

    @GetMapping("/history")
    @Operation(summary = "Get backup history")
    public ResponseEntity<ApiResponse<List<BackupMetadata>>> getBackupHistory() {
        List<BackupMetadata> history = backupService.getBackupHistory();
        return ResponseEntity.ok(ApiResponse.success("Backup history retrieved", history));
    }
}

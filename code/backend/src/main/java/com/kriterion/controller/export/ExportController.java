package com.kriterion.controller.export;

import com.kriterion.entity.enums.TransactionType;
import com.kriterion.service.export.CsvExportService;
import com.kriterion.service.export.PdfReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/exports")
@RequiredArgsConstructor
@Tag(name = "Export", description = "Endpoints for exporting data and reports")
public class ExportController {

    private final CsvExportService csvExportService;
    private final PdfReportService pdfReportService;

    @GetMapping("/transactions/csv")
    @Operation(summary = "Export transactions to CSV")
    public ResponseEntity<byte[]> exportTransactions(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String search) {

        byte[] csvData = csvExportService.exportTransactionsToCsv(categoryId, type, startDate, endDate, search);

        String fileName = "transactions_" + LocalDate.now() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    @GetMapping("/reports/monthly")
    @Operation(summary = "Generate monthly PDF report")
    public ResponseEntity<byte[]> generateMonthlyReport() {
        byte[] pdfData = pdfReportService.generateMonthlyReport();

        String fileName = "monthly_report_" + LocalDate.now().getMonth() + "_" + LocalDate.now().getYear() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }

    @GetMapping("/reports/yearly")
    @Operation(summary = "Generate yearly PDF report")
    public ResponseEntity<byte[]> generateYearlyReport() {
        byte[] pdfData = pdfReportService.generateYearlyReport();

        String fileName = "yearly_report_" + LocalDate.now().getYear() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }
}

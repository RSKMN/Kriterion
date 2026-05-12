package com.kriterion.service.export;

import com.kriterion.analytics.AnalyticsService;
import com.kriterion.dto.analytics.*;
import com.kriterion.dto.budget.BudgetStatusResponse;
import com.kriterion.service.BudgetService;
import com.kriterion.service.RecurringTransactionService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfReportService {

    private final AnalyticsService analyticsService;
    private final BudgetService budgetService;
    private final RecurringTransactionService recurringTransactionService;

    public byte[] generateMonthlyReport() {
        LocalDate now = LocalDate.now();
        DashboardSummaryResponse summary = analyticsService.getDashboardSummary();
        CategoryBreakdownResponse breakdown = analyticsService.getCategoryBreakdown();
        BudgetStatusResponse budgetStatus = budgetService.getBudgetStatus(now.getMonthValue(), now.getYear());
        var recurring = recurringTransactionService.getAllRecurringTransactions();

        return generatePdf("MONTHLY FINANCIAL REPORT", summary, breakdown, budgetStatus, recurring, "Report for " + now.getMonth() + " " + now.getYear());
    }

    public byte[] generateYearlyReport() {
        LocalDate now = LocalDate.now();
        DashboardSummaryResponse summary = analyticsService.getDashboardSummary();
        CategoryBreakdownResponse breakdown = analyticsService.getCategoryBreakdown();
        // For yearly, we might want different status, but for now we use current status
        BudgetStatusResponse budgetStatus = budgetService.getBudgetStatus(now.getMonthValue(), now.getYear());
        var recurring = recurringTransactionService.getAllRecurringTransactions();

        return generatePdf("YEARLY FINANCIAL REVIEW", summary, breakdown, budgetStatus, recurring, "Financial Year " + now.getYear());
    }

    private byte[] generatePdf(String titleText, DashboardSummaryResponse summary, CategoryBreakdownResponse breakdown, 
                              BudgetStatusResponse budgetStatus, List<com.kriterion.dto.recurring.RecurringTransactionResponse> recurring, String subtitle) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(15, 23, 42)); // Slate 900
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(100, 116, 139)); // Slate 500
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new Color(30, 41, 59)); // Slate 800
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(51, 65, 85)); // Slate 700
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 8, new Color(148, 163, 184)); // Slate 400

            // Title
            Paragraph title = new Paragraph(titleText, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph sub = new Paragraph(subtitle, subtitleFont);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(20);
            document.add(sub);

            // Summary Grid (Income, Expense, Balance)
            PdfPTable grid = new PdfPTable(3);
            grid.setWidthPercentage(100);
            grid.setSpacingAfter(20);
            
            addSummaryCard(grid, "TOTAL INCOME", "$" + summary.getTotalIncome(), new Color(34, 197, 94)); // Green 500
            addSummaryCard(grid, "TOTAL EXPENSE", "$" + summary.getTotalExpense(), new Color(239, 68, 68)); // Red 500
            addSummaryCard(grid, "NET BALANCE", "$" + summary.getTotalBalance(), new Color(59, 130, 246)); // Blue 500
            document.add(grid);

            // 1. Spending Breakdown
            addSectionTitle(document, "Spending Breakdown", sectionFont);
            PdfPTable catTable = new PdfPTable(new float[]{4, 3, 3});
            catTable.setWidthPercentage(100);
            catTable.setSpacingAfter(20);
            addTableHeader(catTable, new String[]{"Category", "Amount", "Percentage"}, headerFont);
            for (CategorySpendingResponse cat : breakdown.getCategories()) {
                addTableCell(catTable, cat.getCategoryName(), bodyFont);
                addTableCell(catTable, "$" + cat.getTotalSpent(), bodyFont);
                addTableCell(catTable, cat.getPercentage() + "%", bodyFont);
            }
            document.add(catTable);

            // 2. Budget Performance
            addSectionTitle(document, "Budget Performance", sectionFont);
            PdfPTable budgetTable = new PdfPTable(new float[]{3, 2, 2, 3});
            budgetTable.setWidthPercentage(100);
            budgetTable.setSpacingAfter(20);
            addTableHeader(budgetTable, new String[]{"Budget", "Limit", "Spent", "Status"}, headerFont);
            for (var b : budgetStatus.getBudgets()) {
                addTableCell(budgetTable, b.getCategoryName(), bodyFont);
                addTableCell(budgetTable, "$" + b.getMonthlyLimit(), bodyFont);
                addTableCell(budgetTable, "$" + b.getCurrentSpent(), bodyFont);
                String status = b.isExceeded() ? "EXCEEDED" : (b.isWarning() ? "WARNING" : "HEALTHY");
                addTableCell(budgetTable, status, bodyFont);
            }
            document.add(budgetTable);

            // 3. Recurring Transactions
            if (!recurring.isEmpty()) {
                addSectionTitle(document, "Recurring Payments", sectionFont);
                PdfPTable recTable = new PdfPTable(new float[]{4, 2, 2, 2});
                recTable.setWidthPercentage(100);
                recTable.setSpacingAfter(20);
                addTableHeader(recTable, new String[]{"Title", "Amount", "Frequency", "Next Run"}, headerFont);
                for (var r : recurring) {
                    addTableCell(recTable, r.getTitle(), bodyFont);
                    addTableCell(recTable, "$" + r.getAmount(), bodyFont);
                    addTableCell(recTable, r.getRecurrenceType().toString(), bodyFont);
                    addTableCell(recTable, r.getNextRunDate().toString(), bodyFont);
                }
                document.add(recTable);
            }

            // Footer
            Paragraph footer = new Paragraph("Generated by Kriterion Financial Assistant on " + LocalDate.now() + " - Confidential", smallFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate advanced PDF report", e);
        }
    }

    private void addSectionTitle(Document doc, String title, Font font) throws DocumentException {
        Paragraph p = new Paragraph(title, font);
        p.setSpacingBefore(10);
        p.setSpacingAfter(10);
        doc.add(p);
    }

    private void addSummaryCard(PdfPTable table, String label, String value, Color color) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(10);
        cell.setBackgroundColor(new Color(248, 250, 252)); // Slate 50
        cell.setBorderColor(new Color(226, 232, 240)); // Slate 200
        
        Paragraph pLabel = new Paragraph(label, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, new Color(100, 116, 139)));
        pLabel.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(pLabel);
        
        Paragraph pValue = new Paragraph(value, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, color));
        pValue.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(pValue);
        
        table.addCell(cell);
    }

    private void addTableHeader(PdfPTable table, String[] headers, Font font) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, font));
            cell.setBackgroundColor(new Color(71, 85, 105)); // Slate 600
            cell.setPadding(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        cell.setBorderColor(new Color(241, 245, 249)); // Slate 100
        table.addCell(cell);
    }
}

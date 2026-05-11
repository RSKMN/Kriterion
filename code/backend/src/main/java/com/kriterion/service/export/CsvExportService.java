package com.kriterion.service.export;

import com.kriterion.entity.Transaction;
import com.kriterion.repository.TransactionRepository;
import com.kriterion.repository.specification.TransactionSpecification;
import com.kriterion.security.util.AuthenticationUtil;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CsvExportService {

    private final TransactionRepository transactionRepository;

    public byte[] exportTransactionsToCsv(Long categoryId, com.kriterion.entity.enums.TransactionType type, LocalDate startDate, LocalDate endDate, String search) {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        
        Specification<Transaction> spec = TransactionSpecification.withFilters(userId, categoryId, type, startDate, endDate, search);
        List<Transaction> transactions = transactionRepository.findAll(spec);

        try (StringWriter sw = new StringWriter();
             CSVWriter writer = new CSVWriter(sw)) {
            
            // Header
            String[] header = {"Date", "Title", "Amount", "Type", "Category", "Payment Method", "Merchant", "Description"};
            writer.writeNext(header);

            // Data
            for (Transaction t : transactions) {
                String[] data = {
                    t.getTransactionDate().toString(),
                    t.getTitle(),
                    t.getAmount().toString(),
                    t.getType().toString(),
                    t.getCategory() != null ? t.getCategory().getName() : "N/A",
                    t.getPaymentMethod() != null ? t.getPaymentMethod().toString() : "N/A",
                    t.getMerchantName() != null ? t.getMerchantName() : "N/A",
                    t.getDescription() != null ? t.getDescription() : ""
                };
                writer.writeNext(data);
            }

            return sw.toString().getBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate CSV", e);
        }
    }
}

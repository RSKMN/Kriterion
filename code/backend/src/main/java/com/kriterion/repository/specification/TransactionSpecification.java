package com.kriterion.repository.specification;

import com.kriterion.entity.Transaction;
import com.kriterion.entity.enums.TransactionType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    public static Specification<Transaction> withFilters(Long userId,
                                                         Long categoryId,
                                                         TransactionType type,
                                                         LocalDate startDate,
                                                         LocalDate endDate,
                                                         String search) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("user").get("id"), userId));

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), startDate));
            }

            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), endDate));
            }

            if (search != null && !search.trim().isEmpty()) {
                String likePattern = "%" + search.trim().toLowerCase() + "%";
                Predicate titlePredicate = cb.like(cb.lower(root.get("title")), likePattern);
                Predicate merchantPredicate = cb.like(cb.lower(root.get("merchantName")), likePattern);
                predicates.add(cb.or(titlePredicate, merchantPredicate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

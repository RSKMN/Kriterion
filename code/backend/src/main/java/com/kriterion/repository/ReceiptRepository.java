package com.kriterion.repository;

import com.kriterion.entity.Receipt;
import com.kriterion.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findByFileId(String fileId);
    List<Receipt> findByUserOrderByCreatedAtDesc(User user);
}

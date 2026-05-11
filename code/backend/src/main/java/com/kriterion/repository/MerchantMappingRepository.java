package com.kriterion.repository;

import com.kriterion.entity.MerchantMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantMappingRepository extends JpaRepository<MerchantMapping, Long> {
    Optional<MerchantMapping> findByUserIdAndMerchantName(Long userId, String merchantName);
}

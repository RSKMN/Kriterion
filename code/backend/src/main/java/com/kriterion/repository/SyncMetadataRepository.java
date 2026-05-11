package com.kriterion.repository;

import com.kriterion.entity.SyncMetadata;
import com.kriterion.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SyncMetadataRepository extends JpaRepository<SyncMetadata, Long> {
    Optional<SyncMetadata> findByUserAndDeviceId(User user, String deviceId);
}

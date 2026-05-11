package com.kriterion.repository;

import com.kriterion.entity.MobileSession;
import com.kriterion.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MobileSessionRepository extends JpaRepository<MobileSession, Long> {
    List<MobileSession> findByUserAndActiveTrue(User user);
    Optional<MobileSession> findByUserAndDeviceId(User user, String deviceId);
    Optional<MobileSession> findByFcmToken(String fcmToken);
    
    // Compatibility methods for ID-based lookup if needed
    List<MobileSession> findByUserIdAndActiveTrue(Long userId);
    Optional<MobileSession> findByUserIdAndDeviceId(Long userId, String deviceId);
}

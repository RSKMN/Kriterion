package com.kriterion.repository;

import com.kriterion.entity.FcmToken;
import com.kriterion.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByToken(String token);
    List<FcmToken> findByUserAndActiveTrue(User user);
    Optional<FcmToken> findBySessionId(Long sessionId);
    void deleteByToken(String token);
}

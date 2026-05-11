package com.kriterion.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private boolean revoked = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private MobileSession session;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "replaced_by_token")
    private String replacedByToken;

    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }
}

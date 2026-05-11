package com.kriterion.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fcm_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FcmToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private MobileSession session;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "platform")
    private String platform; // android, ios

    @Column(name = "last_validated_at")
    private LocalDateTime lastValidatedAt;

    @Column(name = "is_active")
    private boolean active = true;
}

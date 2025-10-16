package com.Ali.Store.App.entities.userAndProfileUser;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Service
@ToString
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String token;
    @Column(nullable = false, length = 500, unique = true)
    private String deviceId;
    private String deviceInfo;
    private Instant expiryDate;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    public RefreshToken(Long id, String token, String deviceId, String deviceInfo, Instant expiryDate) {
        this.id = id;
        this.token = token;
        this.deviceId = deviceId;
        this.deviceInfo = deviceInfo;
        this.expiryDate = expiryDate;
    }
}

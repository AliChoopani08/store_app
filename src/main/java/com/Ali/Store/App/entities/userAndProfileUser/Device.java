package com.Ali.Store.App.entities.userAndProfileUser;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.IDENTITY;

@Table(name = "devices")
@Entity
@Builder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Device {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private UUID deviceUuid;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(nullable = false)
    private boolean isAvailable;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @OneToOne(mappedBy = "device", cascade = ALL, orphanRemoval = true)
    private RefreshToken refreshToken;

    public void addRefreshToken(RefreshToken refreshToken) {
        refreshToken.setDevice(this);
        this.setRefreshToken(refreshToken);
    }

}

package com.Ali.Store.App.entities.userAndProfileUser;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Column(nullable = false, updatable = false)
    private UUID token;
    private Instant expiryDate;

    @OneToOne
    @JoinColumn(name = "device_id")
    private Device device;
}

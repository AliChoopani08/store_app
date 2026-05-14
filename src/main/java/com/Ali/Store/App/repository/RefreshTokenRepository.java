package com.Ali.Store.App.repository;

import com.Ali.Store.App.entities.userAndProfileUser.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @Query("""
            SELECT rt
            FROM RefreshToken rt
            WHERE rt.device.deviceUuid = :deviceUuid
            """)
    Optional<RefreshToken> findByDeviceUUid(UUID deviceUuid);

    @Query("""
            SELECT rt
            FROM RefreshToken rt
            JOIN rt.device d
            JOIN d.user u
            WHERE rt.token = :token 
                AND d.deviceUuid = :deviceUuid
                AND d.isAvailable = true
                AND u.id = :userId
            """)
    Optional<RefreshToken> findByTokenAndDeviceAndUser(@Param("token") UUID token, @Param("deviceUuid") UUID deviceUuid, @Param("userId") Long userId);

    @Modifying
    @Query("""
            DELETE FROM RefreshToken rt
            WHERE rt.device.deviceUuid in (
                SELECT d.deviceUuid
                FROM Device d
                WHERE d.deviceUuid = :deviceUuid AND d.user.id = :userId)
            """)
    void deleteByUserIdAndDeviceUuid(@Param("userId") Long userId, @Param("deviceUuid") UUID deviceUuid);

    @Modifying
    @Query("""
            DELETE FROM RefreshToken rt
            WHERE rt.expiryDate < :now
            """)
    void deleteAllExpiredRefreshTokens(@Param("now") Instant now);
}

package com.Ali.Store.App.repository.userAndProfileUser;

import com.Ali.Store.App.entities.userAndProfileUser.RefreshToken;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RepositoryRefreshToken extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenAndDeviceId(String token, String deviceId);
    Optional<RefreshToken> findByDeviceId(String deviceId);

    Optional<RefreshToken> findByUserIdAndDeviceId(Long userId, String deviceId);

    @Modifying
    void deleteByUser(Users users);

    @Modifying
    @Query("""
            DELETE FROM RefreshToken rt
            WHERE rt.expiryDate < :now
            """)
    void deleteAllExpiredRefreshTokens(@Param("now") LocalDateTime now);
}

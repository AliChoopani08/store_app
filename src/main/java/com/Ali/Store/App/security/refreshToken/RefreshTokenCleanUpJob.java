package com.Ali.Store.App.security.refreshToken;

import com.Ali.Store.App.repository.userAndProfileUser.RepositoryRefreshToken;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Delete all expired saved refresh tokens in database every day at 3 o'clock in the midnight
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenCleanUpJob {
    private final RepositoryRefreshToken repositoryRefreshToken;

    @Scheduled(fixedRate = 180000)// every 3 minute
    @Transactional
    public void expiredRefreshTokenCleanUp() {
        repositoryRefreshToken.deleteAllExpiredRefreshTokens(Instant.now());
    }
}

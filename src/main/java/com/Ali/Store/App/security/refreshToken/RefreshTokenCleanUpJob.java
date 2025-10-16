package com.Ali.Store.App.security.refreshToken;

import com.Ali.Store.App.repository.userAndProfileUser.RepositoryRefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import static java.time.LocalDateTime.now;

/**
 * Delete all expired saved refresh tokens in database every day at 3 o'clock in the midnight
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenCleanUpJob {
    private final RepositoryRefreshToken repositoryRefreshToken;

    @Scheduled(cron = "0 0 3 * * ?")
    public void expiredRefreshTokenCleanUp() {
        repositoryRefreshToken.deleteAllExpiredRefreshTokens(now());
    }
}

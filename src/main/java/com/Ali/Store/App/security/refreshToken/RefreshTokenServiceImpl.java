package com.Ali.Store.App.security.refreshToken;

import com.Ali.Store.App.dto.user.request.RefreshTokenRequest;
import com.Ali.Store.App.entities.userAndProfileUser.RefreshToken;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.exceptions.security.NotFoundRefreshToken;
import com.Ali.Store.App.exceptions.user.NotFoundUser;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryRefreshToken;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenServiceInterface {

    private final Duration duration = Duration.ofDays(20); // for 20 days
    private final Instant expiryDate = Instant.now().plus(duration); // plus duration to time now

    private final RepositoryRefreshToken repositoryRefreshToken;
    private final RepositoryUser repositoryUser;


    @Override
    public RefreshToken getByTokenAndDeviceId(RefreshTokenRequest tokenRequest) {
        return repositoryRefreshToken.findByTokenAndDeviceId(tokenRequest.getRefreshToken(), tokenRequest.getDeviceId())
                .orElseThrow(() -> new NotFoundRefreshToken("This refresh token is not exist in database !"));
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Users user, String deviceId, String deviceInfo) {
        RefreshToken refreshToken = new RefreshToken();
        final String createRandomlyToken = UUID.randomUUID().toString();

        user.addRefreshToken(refreshToken);
        refreshToken.setToken(createRandomlyToken);
        refreshToken.setDeviceId(deviceId);
        refreshToken.setDeviceInfo(deviceInfo);
        refreshToken.setExpiryDate(expiryDate);

        return repositoryRefreshToken.save(refreshToken);
    }

    @Override
    public boolean expiredRefreshToken(RefreshTokenRequest tokenRequest) {
        final RefreshToken foundRefreshToken = getByTokenAndDeviceId(tokenRequest);

        return Instant.now().isAfter(foundRefreshToken.getExpiryDate());
    }

    @Override
    @Transactional
    public void deleteByUser(Users user) {
        final Users foundUser = repositoryUser.findByUsername(user.getUsername())
                .orElseThrow(() -> new NotFoundUser("Not found this user! "));

        repositoryRefreshToken.deleteByUser(foundUser);
    }

    @Override
    public void deleteExpiredUser(RefreshTokenRequest tokenRequest) {
        final RefreshToken foundRefreshToken = getByTokenAndDeviceId(tokenRequest);

        repositoryRefreshToken.delete(foundRefreshToken);
    }
}

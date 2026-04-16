package com.Ali.Store.App.security.refreshToken;

import com.Ali.Store.App.dto.user.request.RefreshTokenRequest;
import com.Ali.Store.App.entities.userAndProfileUser.RefreshToken;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.exceptions.DuplicateValueException;
import com.Ali.Store.App.exceptions.security.NotFoundRefreshToken;
import com.Ali.Store.App.exceptions.user.NotFoundUser;
import com.Ali.Store.App.repository.RefreshTokenRepository;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenServiceInterface {

    private final Duration duration = Duration.ofDays(20); // for 20 days
    private final Instant expiryDate = Instant.now().plus(duration); // plus duration to time now

    private final RefreshTokenRepository repositoryRefreshToken;
    private final UserRepository repositoryUser;


    @Override
    public RefreshToken getByTokenAndDeviceId(String token, String deviceId) {
        return repositoryRefreshToken.findByTokenAndDeviceId(token, deviceId)
                .orElseThrow(NotFoundRefreshToken::new);
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Users user, String deviceId, String deviceInfo) {
        final Optional<RefreshToken> byDeviceId = repositoryRefreshToken.findByDeviceId(deviceId);
        if (byDeviceId.isPresent() && !expiredRefreshToken(byDeviceId.get().getToken(), byDeviceId.get().getDeviceId())) {
            throw new DuplicateValueException("This Refresh Token is already active in database. Please generate a new Access Token with this refresh token.");
        }
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
    public boolean expiredRefreshToken(String token, String deviceId) {
        final RefreshToken foundRefreshToken = getByTokenAndDeviceId(token, deviceId);

        return Instant.now().isAfter(foundRefreshToken.getExpiryDate());
    }

    @Override
    @Transactional
    public void deleteByUser(Users user) {
        final String username = user.getUsername();

        final Users foundUser = repositoryUser.findByUsername(username)
                .orElseThrow(() -> new NotFoundUser(username));

        repositoryRefreshToken.deleteByUser(foundUser);
    }

    @Override
    public void deleteExpiredUser(RefreshTokenRequest tokenRequest) {
        final RefreshToken foundRefreshToken = getByTokenAndDeviceId(tokenRequest.getToken(), tokenRequest.getDeviceId());

        repositoryRefreshToken.delete(foundRefreshToken);
    }
}

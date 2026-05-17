package com.Ali.Store.App.service.refreshToken;

import com.Ali.Store.App.dto.user.request.LogoutRequest;
import com.Ali.Store.App.entities.userAndProfileUser.Device;
import com.Ali.Store.App.entities.userAndProfileUser.RefreshToken;
import com.Ali.Store.App.exceptions.security.NotFoundRefreshToken;
import com.Ali.Store.App.exceptions.user.DuplicateRefreshToken;
import com.Ali.Store.App.exceptions.user.NotFoundDevice;
import com.Ali.Store.App.repository.DeviceRepository;
import com.Ali.Store.App.repository.RefreshTokenRepository;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static java.time.Instant.now;
import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenServiceInterface {

    private final Duration duration = Duration.ofDays(30); // for 30 days
    private final Instant expiryDate = now().plus(duration); // plus duration to time now

    private final RefreshTokenRepository repositoryRefreshToken;
    private final UserRepository repositoryUser;
    private final DeviceRepository deviceRepository;


    @Override
    public RefreshToken getByDeviceUuid(UUID deviceUuid) {
        return repositoryRefreshToken.findByDeviceUUid(deviceUuid)
                .orElseThrow(NotFoundRefreshToken::new);
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(UUID deviceUuid, Long userId) {
        final Device foundDevice = deviceRepository.findByDeviceUuidAndUserId(deviceUuid, userId)
                        .map(d -> {
                            repositoryRefreshToken.findByDeviceUUid(deviceUuid)
                                .ifPresent(rt -> {
                                    if (!expiredRefreshToken(rt)) {
                                        throw new DuplicateRefreshToken();
                                    }
                                });

                            RefreshToken refreshToken = RefreshToken.builder()
                                    .token(randomUUID())
                                    .expiryDate(expiryDate)
                                    .build();
                            d.addRefreshToken(refreshToken);

                            return deviceRepository.save(d);
                        })
                .orElseThrow(() -> new NotFoundDevice(deviceUuid));

        return foundDevice.getRefreshToken();
    }

    @Override
    public boolean expiredRefreshToken(RefreshToken refreshToken) {

        return now().isAfter(refreshToken.getExpiryDate());
    }

    @Override
    @Transactional
    public void removeRefreshTokenByUserIdAndDeviceUuid(LogoutRequest logoutReq) {
        repositoryRefreshToken.deleteByUserIdAndDeviceUuid(logoutReq.getUserId(), logoutReq.getDeviceUuid());
    }

    @Override
    public void deleteExpiredRefreshTokenByDeviceUUid(RefreshToken refreshToken) {

        repositoryRefreshToken.delete(refreshToken);
    }
}

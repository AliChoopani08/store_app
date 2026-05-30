package com.Ali.Store.App.service.refreshToken;

import com.Ali.Store.App.dto.user.request.LogoutRequest;
import com.Ali.Store.App.entities.userAndProfileUser.RefreshToken;

import java.util.UUID;

public interface RefreshTokenServiceInterface {


    RefreshToken getByDeviceUuid(UUID deviceUuid);
    RefreshToken createRefreshToken(UUID deviceUuid, Long userId);
    boolean expiredRefreshToken(RefreshToken refreshToken);
    void removeRefreshTokenByUserIdAndDeviceUuid(LogoutRequest logoutReq);
    void deleteExpiredRefreshTokenByDeviceUUid(RefreshToken refreshToken);
}

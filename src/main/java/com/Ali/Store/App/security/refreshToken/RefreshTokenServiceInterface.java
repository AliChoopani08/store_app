package com.Ali.Store.App.security.refreshToken;

import com.Ali.Store.App.dto.user.request.LogoutRequest;
import com.Ali.Store.App.entities.userAndProfileUser.RefreshToken;

import java.util.UUID;

public interface RefreshTokenServiceInterface {

    RefreshToken getByDeviceUuid(UUID deviceUuid);
    RefreshToken createRefreshToken(UUID deviceUuid);
    boolean expiredRefreshToken(UUID deviceUuid);
    void deleteByDeviceUuid(LogoutRequest logoutReq);
    void deleteExpiredRefreshTokenByDeviceUUid(UUID deviceUuid);
}

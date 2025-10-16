package com.Ali.Store.App.security.refreshToken;

import com.Ali.Store.App.dto.user.request.RefreshTokenRequest;
import com.Ali.Store.App.entities.userAndProfileUser.RefreshToken;
import com.Ali.Store.App.entities.userAndProfileUser.Users;

public interface RefreshTokenServiceInterface {

    RefreshToken getByTokenAndDeviceId(RefreshTokenRequest tokenRequest);
    RefreshToken createRefreshToken(Users user, String deviceId, String deviceInfo);
    boolean expiredRefreshToken(RefreshTokenRequest tokenRequest);
    void deleteByUser(Users user);
    void deleteExpiredUser(RefreshTokenRequest tokenRequest);
}

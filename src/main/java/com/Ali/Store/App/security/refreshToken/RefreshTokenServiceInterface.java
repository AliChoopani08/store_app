package com.Ali.Store.App.security.refreshToken;

import com.Ali.Store.App.dto.user.request.RefreshTokenRequest;
import com.Ali.Store.App.entities.userAndProfileUser.RefreshToken;
import com.Ali.Store.App.entities.userAndProfileUser.Users;

public interface RefreshTokenServiceInterface {

    RefreshToken getByTokenAndDeviceId(String token, String deviceId);
    RefreshToken createRefreshToken(Users user, String deviceId, String deviceInfo);
    boolean expiredRefreshToken(String token, String deviceId);
    void deleteByUser(Users user);
    void deleteExpiredUser(RefreshTokenRequest tokenRequest);
}

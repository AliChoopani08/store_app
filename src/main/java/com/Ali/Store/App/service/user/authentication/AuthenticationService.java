package com.Ali.Store.App.service.user.authentication;

import com.Ali.Store.App.dto.security.PwdVerifyJwtResponse;
import com.Ali.Store.App.dto.user.request.*;
import com.Ali.Store.App.dto.user.response.UserSummary;
import com.Ali.Store.App.dto.security.JwtAuthResponse;

public interface AuthenticationService {

    JwtAuthResponse saveUser(UserRequest userRequest);
    JwtAuthResponse saveAdmin(CreateAdminRequest createAdminRequest, String deviceInfo);
    JwtAuthResponse login(UserRequest userRequest, String deviceInfo);
    void logout(Long userId);
    UserSummary changeUsername(Long userId, ChangeUsernameRequest changeUsernameRequest);
    PwdVerifyJwtResponse passwordVerify(Long userId, PasswordVerifyRequest passwordVerifyRequest);
    UserSummary passwordReset(PasswordResetRequest passwordResetRequest);
    JwtAuthResponse createNewAccessToken(RefreshTokenRequest refreshTokenRequest);
}

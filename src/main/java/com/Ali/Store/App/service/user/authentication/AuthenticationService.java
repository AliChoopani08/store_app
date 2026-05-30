package com.Ali.Store.App.service.user.authentication;

import com.Ali.Store.App.dto.security.PwdVerifyJwtResponse;
import com.Ali.Store.App.dto.user.request.*;
import com.Ali.Store.App.dto.user.response.UserSummary;
import com.Ali.Store.App.dto.security.AuthResponse;

import java.util.UUID;

public interface AuthenticationService {

    AuthResponse saveUser(RegisterUserRequest userRequest, String deviceInfo);
    AuthResponse saveAdmin(CreateAdminRequest createAdminRequest, String deviceInfo);
    AuthResponse login(LoginUserRequest loginUserRequest, UUID deviceUuid);
    void logout(LogoutRequest logoutRequest);
    UserSummary changeUsername(Long userId, ChangeUsernameRequest changeUsernameRequest);
    PwdVerifyJwtResponse passwordVerifyAndGenerateAPasswwordVerifyToken(Long userId, PasswordVerifyRequest passwordVerifyRequest);
    UserSummary passwordReset(PasswordResetRequest passwordResetRequest);
    AuthResponse createNewAccessToken(UUID deviceUuid, RefreshTokenRequest refreshTokenRequest);
}

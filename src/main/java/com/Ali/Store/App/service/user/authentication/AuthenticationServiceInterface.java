package com.Ali.Store.App.service.user.authentication;

import com.Ali.Store.App.dto.security.PwdVerifyJwtResponse;
import com.Ali.Store.App.dto.user.request.*;
import com.Ali.Store.App.dto.user.response.UserResponse;
import com.Ali.Store.App.dto.security.AuthJwtResponse;

public interface AuthenticationServiceInterface {

    AuthJwtResponse saveUser(UserRequest userRequest, String deviceInfo);
    AuthJwtResponse saveAdmin(CreateAdminRequest createAdminRequest, String deviceInfo);
    AuthJwtResponse login(UserRequest userRequest, String deviceInfo);
    void logout(Long userId);
    UserResponse changeUsername(Long userId, ChangeUsernameRequest changeUsernameRequest);
    PwdVerifyJwtResponse passwordVerify(Long userId, PasswordVerifyRequest passwordVerifyRequest);
    UserResponse passwordReset(PasswordResetRequest passwordResetRequest);
    AuthJwtResponse createNewAccessToken(RefreshTokenRequest refreshTokenRequest);
}

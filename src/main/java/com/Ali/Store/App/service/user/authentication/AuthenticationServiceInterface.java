package com.Ali.Store.App.service.user.authentication;

import com.Ali.Store.App.dto.user.request.*;
import com.Ali.Store.App.dto.user.response.UserResponse;
import com.Ali.Store.App.security.jwt.JwtResponse;

public interface AuthenticationServiceInterface {

    JwtResponse saveUser(UserRequest userRequest, String deviceInfo);
    JwtResponse saveAdmin(CreateAdminRequest createAdminRequest, String deviceInfo);
    JwtResponse login(UserRequest userRequest, String deviceInfo);
    void logout(Long userId);
    /*JwtResponse changeUsername(Long userId, ChangeUsernameRequest)*/
    JwtResponse createNewAccessToken(RefreshTokenRequest refreshTokenRequest);
}

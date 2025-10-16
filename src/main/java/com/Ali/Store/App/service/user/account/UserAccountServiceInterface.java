package com.Ali.Store.App.service.user.account;

import com.Ali.Store.App.dto.user.request.ProfileRequest;
import com.Ali.Store.App.dto.user.response.UserResponse;

public interface UserAccountServiceInterface {

    UserResponse updateProfile(Long userId, ProfileRequest profileRequest);
    void deleteAccount(Long userId);
    UserResponse displayProfile(Long userId);
}

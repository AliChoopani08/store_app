package com.Ali.Store.App.service.user.account;

import com.Ali.Store.App.dto.user.request.ProfileRequest;
import com.Ali.Store.App.dto.user.response.UserSummary;

public interface UserAccountService {

    UserSummary updateProfile(Long userId, ProfileRequest profileRequest);
    void disableAccount(Long userId);
    UserSummary displayProfile(Long userId);
}

package com.Ali.Store.App.service.admin;

import com.Ali.Store.App.dto.user.request.ChangeRoleRequest;
import com.Ali.Store.App.dto.user.request.SearchUserRequest;
import com.Ali.Store.App.dto.user.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ManageUsersServiceInterface {
    Page<UserResponse> searchUsers(SearchUserRequest searchUserRequest, Pageable pageable);
    UserResponse findUserById(Long id);
    void deleteUserById(Long id);
    UserResponse changeRoleOfUser(Long id, ChangeRoleRequest changeRoleRequest);
}

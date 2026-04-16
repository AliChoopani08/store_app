package com.Ali.Store.App.service.admin;

import com.Ali.Store.App.dto.user.request.ChangeRoleRequest;
import com.Ali.Store.App.dto.user.request.SearchUserRequest;
import com.Ali.Store.App.dto.user.response.UserSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ManageUsersService {
    Page<UserSummary> searchUsers(SearchUserRequest searchUserRequest, Pageable pageable);
    UserSummary findUserById(Long id);
    void disActiveUserById(Long id);
    UserSummary changeRoleOfUser(Long id, ChangeRoleRequest changeRoleRequest);
}

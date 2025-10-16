package com.Ali.Store.App.service.admin;

import com.Ali.Store.App.dto.user.request.ChangeRoleRequest;
import com.Ali.Store.App.dto.user.response.ProfileResponse;
import com.Ali.Store.App.dto.user.request.SearchUserRequest;
import com.Ali.Store.App.dto.user.UserMapper;
import com.Ali.Store.App.dto.user.response.UserResponse;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.exceptions.user.NotFoundUser;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.Ali.Store.App.entities.userAndProfileUser.Role.valueOf;

@Service
public class ManageUserServiceImpl implements ManageUsersServiceInterface{

    private final RepositoryUser repositoryUser;
    private final UserMapper userMapper;

    public ManageUserServiceImpl(RepositoryUser repositoryUser, UserMapper userMapper) {
        this.repositoryUser = repositoryUser;
        this.userMapper = userMapper;
    }

    @Override
    public Page<UserResponse> searchUsers(SearchUserRequest searchUserRequest, Pageable pageable) {
        Specification<Users> spec = UsersSpecification
                .withUsername(searchUserRequest.username());

        return repositoryUser.findAll(spec, pageable)
                .map(this::getUserResponse);
    }

    @Override
    public UserResponse findUserById(Long id) {
        final Users foundUser = getUserById(id);

        return getUserResponse(foundUser);
    }

    @Override
    public void deleteUserById(Long id) {
        final Users foundUser = getUserById(id);

        repositoryUser.delete(foundUser);
    }



    @Override
    public UserResponse changeRoleOfUser(Long id, ChangeRoleRequest changeRoleRequest) {
        final Users foundUser = getUserById(id);

        foundUser.setRole(valueOf(changeRoleRequest.getNewRole()));
        final Users savedUser = repositoryUser.save(foundUser);

        return getUserResponse(savedUser);
    }

    private UserResponse getUserResponse(Users user) {
        final ProfileResponse profileResponse = userMapper.profileUserToProfileResponse(user.getProfile());

        return new UserResponse(user.getId()
                , user.getUsername()
                , user.getRole().name()
                , profileResponse);
    }

    private Users getUserById(Long id) {
        return repositoryUser.findById(id)
                .orElseThrow(() -> new NotFoundUser("Not found user with this id !"));
    }
}

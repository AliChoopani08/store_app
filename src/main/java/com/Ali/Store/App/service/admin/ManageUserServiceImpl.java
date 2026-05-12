package com.Ali.Store.App.service.admin;

import com.Ali.Store.App.dto.user.request.ChangeRoleRequest;
import com.Ali.Store.App.dto.user.request.SearchUserRequest;
import com.Ali.Store.App.dto.user.UserMapper;
import com.Ali.Store.App.dto.user.response.UserSummary;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.exceptions.user.NotFoundUser;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import com.Ali.Store.App.service.product.AlwaysTrueSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

import static com.Ali.Store.App.entities.userAndProfileUser.Role.valueOf;
import static com.Ali.Store.App.service.admin.UsersSpecification.withUsername;
import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class ManageUserServiceImpl implements ManageUsersService {

    private final UserRepository repositoryUser;
    private final UserMapper userMapper;

    @Override
    public Page<UserSummary> searchUsers(SearchUserRequest searchUserRequest, Pageable pageable) {
        List<Specification<Users>> specs = new LinkedList<>();

        ofNullable(searchUserRequest.username())
                .ifPresent(u -> specs.add(withUsername(u)));
        final Specification<Users> finalSpec = specs.stream()
                .reduce(new AlwaysTrueSpecification<>(), Specification::and);

        return repositoryUser.findAll(finalSpec, pageable)
                .map(userMapper::toSummary);
    }

    @Override
    public UserSummary findUserById(Long id) {
        final Users foundUser = getUserById(id);

        return userMapper.toSummary(foundUser);
    }

    @Override
    public void disActiveUserById(Long id) {
        final Users foundUser = getUserById(id);

        foundUser.setStatus(false);

        repositoryUser.save(foundUser);
    }



    @Override
    public UserSummary changeRoleOfUser(Long id, ChangeRoleRequest changeRoleRequest) {
        final Users foundUser = getUserById(id);

        foundUser.setRole(valueOf(changeRoleRequest.getNewRole()));
        final Users savedUser = repositoryUser.save(foundUser);

        return userMapper.toSummary(savedUser);
    }


    private Users getUserById(Long id) {
        return repositoryUser.findById(id)
                .orElseThrow(() -> new NotFoundUser(id));
    }
}

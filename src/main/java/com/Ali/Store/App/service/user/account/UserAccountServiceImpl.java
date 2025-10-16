package com.Ali.Store.App.service.user.account;

import com.Ali.Store.App.dto.user.UserMapper;
import com.Ali.Store.App.dto.user.request.ProfileRequest;
import com.Ali.Store.App.dto.user.response.ProfileResponse;
import com.Ali.Store.App.dto.user.response.UserResponse;
import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.exceptions.user.NotFoundUser;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountServiceInterface{

    private final RepositoryUser repository;
    private final UserMapper userMapper;


    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, ProfileRequest usersRequest) {
        final Users currentUser = getCurrentUserById(userId);

        final ProfileUser currentUserProfile = currentUser.getProfile();
        final ProfileUser updatedProfile = userMapper.update(currentUserProfile, usersRequest);
        repository.save(updatedProfile.getUser());

        return getUserResponse(updatedProfile.getUser());
    }

    @Override
    @Transactional
    public void deleteAccount(Long userId) {
        final Users currentUser = getCurrentUserById(userId);

        SecurityContextHolder.clearContext();
        repository.deleteById(currentUser.getId());
    }

    @Override
    public UserResponse displayProfile(Long userId) {
        final Users currentUser = getCurrentUserById(userId);
        return getUserResponse(currentUser);
    }

    private Users getCurrentUserById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundUser("This user doesn't exist in database !"));
    }

    private UserResponse getUserResponse(Users savedUser) {
        final ProfileResponse profileResponse = userMapper.profileUserToProfileResponse(savedUser.getProfile());

        return new UserResponse(savedUser.getId()
                , savedUser.getUsername()
                , savedUser.getRole().name()
                , profileResponse);
    }
}

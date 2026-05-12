package com.Ali.Store.App.service.user.account;

import com.Ali.Store.App.dto.user.UserMapper;
import com.Ali.Store.App.dto.user.request.ProfileRequest;
import com.Ali.Store.App.dto.user.response.ProfileSummary;
import com.Ali.Store.App.dto.user.response.UserSummary;
import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.exceptions.user.NotFoundUser;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserRepository repository;
    private final UserMapper mapper;


    @Override
    @Transactional
    public UserSummary updateProfile(Long userId, ProfileRequest req) {
        final Users currentUser = getCurrentUserById(userId);

        final ProfileUser currentUserProfile = currentUser.getProfile();
        final ProfileUser updatedProfile = mapper.update(currentUserProfile, req);
        repository.save(updatedProfile.getUser());

        return mapper.toSummary(updatedProfile.getUser());
    }

    @Override
    @Transactional
    public void disableAccount(Long userId) {
        final Users currentUser = getCurrentUserById(userId);

        SecurityContextHolder.clearContext();
        currentUser.setStatus(false);

        repository.save(currentUser);
    }

    @Override
    public UserSummary displayProfile(Long userId) {
        final Users currentUser = getCurrentUserById(userId);
        return getUserResponse(currentUser);
    }

    private Users getCurrentUserById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundUser(userId));
    }

    private UserSummary getUserResponse(Users savedUser) {
        final ProfileSummary profileResponse = mapper.toSummary(savedUser.getProfile());

        return new UserSummary(savedUser.getId()
                , savedUser.getUsername()
                , savedUser.getRole().name()
                , profileResponse);
    }
}

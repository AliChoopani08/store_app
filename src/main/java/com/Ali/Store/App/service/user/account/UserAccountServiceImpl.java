package com.Ali.Store.App.service.user.account;

import com.Ali.Store.App.dto.user.UserMapper;
import com.Ali.Store.App.dto.user.request.ProfileRequest;
import com.Ali.Store.App.dto.user.response.UserSummary;
import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.exceptions.user.NotFoundUser;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
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

        log.info("Profile user [{}] updated successfully", currentUser.getId());
        return mapper.toSummary(updatedProfile.getUser());
    }

    @Override
    @Transactional
    public void disableAccount(Long userId) {
        final Users currentUser = getCurrentUserById(userId);

        SecurityContextHolder.clearContext();
        currentUser.setStatus(false);

        log.info("User [{}] account disabled successfully", currentUser.getId());
        repository.save(currentUser);
    }

    @Override
    public UserSummary displayProfile(Long userId) {
        final Users currentUser = getCurrentUserById(userId);

        return mapper.toSummary(currentUser);
    }

    private Users getCurrentUserById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundUser(userId));
    }
}

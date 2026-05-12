package com.Ali.Store.App.user.service.userAccountTest;

import com.Ali.Store.App.dto.user.UserMapper;
import com.Ali.Store.App.dto.user.request.ProfileRequest;
import com.Ali.Store.App.dto.user.response.ProfileSummary;
import com.Ali.Store.App.dto.user.response.UserSummary;
import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import com.Ali.Store.App.service.user.account.UserAccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static com.Ali.Store.App.testHelpers.WhenHelper.whenHelper;
import static com.Ali.Store.App.entities.userAndProfileUser.Role.ROLE_USER;
import static java.time.LocalDate.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAccountServiceTest {

   @Mock
   private UserRepository repository;
   @Mock
   private UserMapper mapper;
   @InjectMocks
    private UserAccountServiceImpl service;

    private Users user;
    private UserSummary summary;

    @BeforeEach
    void setUp() {
        user = Users.builder()
                .id(1L)
                .username("09876543210")
                .role(ROLE_USER)
                .status(true)
                .build();
        ProfileUser profile = ProfileUser.builder()
                .id(1L)
                .build();
        user.setProfileFields(profile);

        ProfileSummary profileSummary = ProfileSummary.builder()
                .name("Ali")
                .birthData(of(2008, 3, 12))
                .build();
        summary = UserSummary.builder()
                .id(1L)
                .username("09876543210")
                .role(ROLE_USER.name())
                .profileSummary(profileSummary)
                .build();
    }

    @Test
    void shouldUpdateProfile_whenUserExists() {
        final Long userId = user.getId();
       ProfileRequest req = ProfileRequest.builder()
                .name("Ali")
                .birthData(of(2008, 3, 12))
                .build();
        ProfileUser updatedProfile = ProfileUser.builder()
                .name("Ali")
                .build();
        user.setProfileFields(updatedProfile);

        whenHelper(repository.findById(anyLong()), Optional.of(user));
        whenHelper(mapper.update(any(ProfileUser.class), any(ProfileRequest.class)), updatedProfile);
        whenHelper(repository.save(any(Users.class)), user);
        whenHelper(mapper.toSummary(any(Users.class)), summary);

        final UserSummary finalUpdatedUser = service.updateProfile(userId, req);
        assertThat(finalUpdatedUser)
                .extracting(UserSummary::username, p -> p.profileSummary().name())
                .containsExactly("09876543210", "Ali");
    }

    @Test
    void shouldDisableUser_whenUserExists() {
        final Long userId = user.getId();
        Users disabledUser = user.toBuilder()
                .status(false)
                .build();

        whenHelper(repository.findById(anyLong()), Optional.of(user));
        whenHelper(repository.save(any(Users.class)), disabledUser);
        service.disableAccount(userId);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();
        verify(repository).save(any(Users.class));
    }
}

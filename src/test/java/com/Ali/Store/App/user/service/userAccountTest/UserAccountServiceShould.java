package com.Ali.Store.App.user.service.userAccountTest;

import com.Ali.Store.App.dto.user.UserMapper;
import com.Ali.Store.App.dto.user.request.ProfileRequest;
import com.Ali.Store.App.dto.user.response.ProfileResponse;
import com.Ali.Store.App.dto.user.response.UserResponse;
import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryUser;
import com.Ali.Store.App.service.user.account.UserAccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static com.Ali.Store.App.entities.userAndProfileUser.Role.ROLE_USER;
import static java.time.LocalDate.of;
import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAccountServiceShould {

   @Mock
   private RepositoryUser repository;
   @Mock
   private UserMapper userMapper;
   @InjectMocks
    private UserAccountServiceImpl service;

    private Users user;
    private ProfileUser oldProfile;
    private ProfileRequest profileRequest;
    private ProfileResponse profileResponse;

    @BeforeEach
    void setUp() {
        user = new Users("09214893654", "Alifghd@54t");
        user.setId(12L);
        user.setRole(ROLE_USER);
        oldProfile = new ProfileUser(null,"09214893654", null, null);
        profileRequest = new ProfileRequest("Ali", null, null, of(2008, 3, 12));
        profileResponse = new ProfileResponse(3L, "Ali", "09214893654", null);
    }

    @Test
    void update_user() {
        final Users mockedCurrentUser = mockFoundUserById();
        mockedCurrentUser.setProfileFields(oldProfile);

        when(userMapper.profileUserToProfileResponse(any(ProfileUser.class)))
                .thenReturn(profileResponse);
        when(userMapper.update(any(ProfileUser.class), eq(profileRequest)))
                .thenAnswer(invocationOnMock -> {
                    final ProfileUser target = invocationOnMock.getArgument(0);
                    final ProfileRequest req = invocationOnMock.getArgument(1);

                    mockProfileUpdateBehavior(target, req);

                    return target;
                });
        when(repository.save(any(Users.class)))
                .thenReturn(mockedCurrentUser);

        final UserResponse finalUpdatedUser = service.updateProfile(12L, profileRequest);
        assertThat(finalUpdatedUser)
                .extracting(UserResponse::username,p -> p.profileResponse().name())
                .containsExactly("09214893654", "Ali");
    }

    @Test
    void delete_account() {
        mockFoundUserById();
        doNothing().when(repository).deleteById(user.getId());
        service.deleteAccount(12L);

        verify(repository).deleteById(user.getId());
    }

    private Users mockFoundUserById() {
        when(repository.findById(any(Long.class)))
                .thenReturn(ofNullable(user));
        return user;
    }


    private void mockProfileUpdateBehavior(ProfileUser profileUser, ProfileRequest profileRequest) {
        if (profileRequest.getName() != null)
            profileUser.setName(profileRequest.getName());

        if (profileRequest.getPhoneNumber() != null)
            profileUser.setPhoneNumber(profileRequest.getPhoneNumber());

        if (profileRequest.getEmail() != null)
            profileUser.setEmail(profileRequest.getEmail());

        if (profileRequest.getBirthData() != null)
            profileUser.setBirthData(profileRequest.getBirthData());
    }
}

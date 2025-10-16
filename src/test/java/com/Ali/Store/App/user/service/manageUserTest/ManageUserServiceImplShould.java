package com.Ali.Store.App.user.service.manageUserTest;

import com.Ali.Store.App.dto.user.response.ProfileResponse;
import com.Ali.Store.App.dto.user.request.SearchUserRequest;
import com.Ali.Store.App.dto.user.UserMapper;
import com.Ali.Store.App.dto.user.response.UserResponse;
import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryUser;
import com.Ali.Store.App.service.admin.ManageUserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;
import static com.Ali.Store.App.entities.userAndProfileUser.Role.ROLE_USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ManageUserServiceImplShould {

    @Mock
    private RepositoryUser repositoryUser;
    @Mock
    private UserMapper mapper;
    @InjectMocks
    private ManageUserServiceImpl service;

    @Test
    void search_user_by_entered_field() {
        SearchUserRequest searchRequest = new SearchUserRequest("0978");
        final ProfileResponse profileResponse = new ProfileResponse(3L, "Ali", "09783564534", null);
        ProfileUser profileUser = new ProfileUser("Ali", "09783564534", null, null);
        Users expectedUser = new Users(3L, "09783564534", ROLE_USER);
        expectedUser.setProfileFields(profileUser);
        Page<Users> mockPage = new PageImpl<>(List.of(expectedUser));

        given(repositoryUser.findAll(any(Specification.class), any(Pageable.class)))
                .willReturn(mockPage);
        given(mapper.profileUserToProfileResponse(any(ProfileUser.class)))
                .willReturn(profileResponse);

        final Page<UserResponse> userResponses = service.searchUsers(searchRequest, Pageable.ofSize(3));

        Assertions.assertThat(userResponses.getContent().getFirst())
                .extracting(UserResponse::username, UserResponse::id)
                .containsExactly("09783564534", 3L);
    }
}

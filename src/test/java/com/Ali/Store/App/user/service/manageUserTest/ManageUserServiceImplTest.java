package com.Ali.Store.App.user.service.manageUserTest;

import com.Ali.Store.App.dto.user.request.SearchUserRequest;
import com.Ali.Store.App.dto.user.UserMapper;
import com.Ali.Store.App.dto.user.response.UserSummary;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import com.Ali.Store.App.service.admin.ManageUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static com.Ali.Store.App.testHelpers.WhenHelper.whenHelper;
import static com.Ali.Store.App.entities.userAndProfileUser.Role.ROLE_USER;
import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.data.domain.Pageable.ofSize;

@ExtendWith(MockitoExtension.class)
public class ManageUserServiceImplTest {

    @Mock
    private UserRepository repositoryUser;
    @Mock
    private UserMapper mapper;
    @InjectMocks
    private ManageUserServiceImpl service;

    private Users user;
    private UserSummary response;

    @BeforeEach
    void setUp() {
        user = Users.builder()
                .id(1L)
                .username("09876543210")
                .role(ROLE_USER)
                .build();
        response = UserSummary.builder()
                .id(1L)
                .username("09876543210")
                .role(ROLE_USER.name())
                .build();
    }

    @Test
    void shouldSearchUser_byUsername_whenUserExists() {
        SearchUserRequest searchRequest = new SearchUserRequest("0987");
        Page<Users> expectedPage = new PageImpl<>(of(user));

        whenHelper(repositoryUser.findAll(any(Specification.class), any(Pageable.class)), expectedPage);
        whenHelper(mapper.toSummary(any(Users.class)), response);

        final Page<UserSummary> userResponses = service.searchUsers(searchRequest, ofSize(3));

        assertThat(userResponses.getContent().getFirst())
                .extracting(UserSummary::username, UserSummary::id)
                .containsExactly("09876543210", 1L);
    }
}

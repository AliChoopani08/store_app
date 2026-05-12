package com.Ali.Store.App.user.userMapper;

import com.Ali.Store.App.dto.user.UserMapper;
import com.Ali.Store.App.dto.user.response.UserSummary;
import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.security.userDetails.UserDetailsImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static com.Ali.Store.App.entities.userAndProfileUser.Role.ROLE_USER;
import static java.time.LocalDate.of;
import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);


    @Test
    void shouldMapUserDetailsToUser() {
        final UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("09112223344")
                .authorities(List.of(new SimpleGrantedAuthority(ROLE_USER.name())))
                .build();

        final Users user = mapper.userDetailsImplToUsers(userDetails);

        Assertions.assertThat(user)
                .extracting(Users::getUsername, u -> u.getRole().name())
                .containsExactly("09112223344", "ROLE_USER");
    }

    @Test
    void shouldMapUserToSummary() {
        ProfileUser profileUser = ProfileUser.builder()
                .name("Ali")
                .birthData(of(2008,3,12))
                .build();
        Users user = Users.builder()
                .id(1L)
                .username("09876543210")
                .role(ROLE_USER)
                .build();
        user.setProfileFields(profileUser);

        final UserSummary summary = mapper.toSummary(user);

        assertThat(summary)
                .extracting(UserSummary::username, UserSummary::role, u -> u.profileSummary().name())
                .containsExactly("09876543210", "ROLE_USER", "Ali");
    }
}

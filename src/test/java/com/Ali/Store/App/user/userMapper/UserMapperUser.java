package com.Ali.Store.App.user.userMapper;

import com.Ali.Store.App.dto.user.UserMapper;
import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.security.userDetails.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;

import static com.Ali.Store.App.entities.userAndProfileUser.Role.ROLE_USER;
import static java.util.Collections.singleton;

public class UserMapperUser {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void mapping_userDatilsImpl_to_users() {
        final ProfileUser profileUser = new ProfileUser("Javad", null, null, LocalDate.of(2012, 1,29));
        final UserDetailsImpl userDetails = new UserDetailsImpl("09112223344", "password", singleton(new SimpleGrantedAuthority(ROLE_USER.name())), 10L, profileUser);

        final Users users = userMapper.userDetailsImplToUsers(userDetails);

        System.out.println(users.getId() + users.getPassword() + users.getRole() + users.getUsername());
    }
}

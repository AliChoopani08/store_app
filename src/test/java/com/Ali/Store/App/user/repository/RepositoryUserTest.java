package com.Ali.Store.App.user.repository;

import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryUser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RepositoryUserTest {

    @Autowired
    private RepositoryUser repositoryUser;


    @BeforeEach
    void setUp() {
        repositoryUser.deleteAll();
    }

    @Test
    void save_user() {
        final Users aliChoopani = new Users("09212575667", "ahdjdh435A");
        aliChoopani.setCreatedAt(now());
        ProfileUser profileUser = new ProfileUser();

        aliChoopani.setProfileFields(profileUser);
        final Users savedUser = repositoryUser.save(aliChoopani);

        assertThat(savedUser)
                .extracting(Users::getUsername, Users::getPassword)
                .containsExactly("09212575667", "ahdjdh435A");
    }

    @Test
    void find_by_username() {
        final String username = "chopaniali373@gmail.com";
        final Users aliChoopani = new Users(username, "ahdjdh435A");
        aliChoopani.setCreatedAt(now());
        ProfileUser profileUser = new ProfileUser();

        profileUser.setName("Ali");
        aliChoopani.setProfileFields(profileUser);
        repositoryUser.save(aliChoopani);
        final Optional<Users> foundUserByUsername = repositoryUser.findByUsername(username);
        final ProfileUser profileFoundUser = foundUserByUsername.get().getProfile();

        assertThat(foundUserByUsername.get()).isEqualTo(aliChoopani);
        assertThat(profileFoundUser)
                .extracting(ProfileUser::getName, ProfileUser::getEmail)
                .containsExactly("Ali", "chopaniali373@gmail.com");
    }

    @Test
    void test_current_transform_fields_from_user_to_profileUser() {
        Users user = new Users("09330526589", "shygGg645");
        ProfileUser profileUser = new ProfileUser();

        user.setProfileFields(profileUser);

        final ProfileUser expectedProfile = new ProfileUser(null, "09330526589", null, null);
        Assertions.assertThat(profileUser).isEqualTo(expectedProfile);

    }
}

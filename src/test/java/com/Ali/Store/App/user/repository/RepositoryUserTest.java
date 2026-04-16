package com.Ali.Store.App.user.repository;

import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class RepositoryUserTest {

    @Autowired
    private UserRepository repository;

    private Users user;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        Users user = Users.builder()
                .username("ali123456789@gmail.com")
                .createdAt(now())
                .build();
        this.user = repository.save(user);
    }

    @Test
    void shouldFindUser_byUsername_whenExists() {
        final String username = user.getUsername();

        final Optional<Users> foundUser = repository.findByUsername(username);

        foundUser.ifPresent(u ->
                assertThat(u.getUsername())
                        .isEqualTo(username));
    }
}

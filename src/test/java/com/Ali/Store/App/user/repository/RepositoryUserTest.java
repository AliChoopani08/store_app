package com.Ali.Store.App.user.repository;

import com.Ali.Store.App.entities.userAndProfileUser.Device;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class RepositoryUserTest {

    @Autowired
    private UserRepository repository;

    private Users user;
    private Device device;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        Users user = Users.builder()
                .username("ali123456789@gmail.com")
                .createdAt(now())
                .status(true)
                .build();

        device = Device.builder()
                .deviceUuid(randomUUID())
                .deviceInfo("acer 315-55kg")
                .isAvailable(true)
                .build();
        user.addDevice(device);

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

    @Test
    @Transactional(readOnly = true)
    void shouldFindUser_byUsernameAndDeviceUuid_whenBeAvailable() {
        final String username = user.getUsername();
        final UUID deviceUuid = device.getDeviceUuid();

        final Optional<Users> foundUser = repository.findByUsernameAndDeviceUuidAndIsAvailable(username, deviceUuid);

        foundUser.ifPresent(user -> assertThat(user)
                .extracting(Users::getUsername, u -> u.getDevices().stream().findFirst().get().getDeviceInfo())
                .containsExactly("ali123456789@gmail.com","acer 315-55kg"));
    }
}

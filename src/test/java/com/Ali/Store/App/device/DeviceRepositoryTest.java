package com.Ali.Store.App.device;

import com.Ali.Store.App.entities.userAndProfileUser.Device;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.DeviceRepository;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class DeviceRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeviceRepository deviceRepository;

    private Long userId;
    private UUID deviceUuid;

    @BeforeEach
    void setUp() {
        deviceUuid = randomUUID();

        Users user = Users.builder()
                .username("Akbar.mohammadi.1356@gmail.com")
                .createdAt(now())
                .build();
        user.addDevice(Device.builder()
                .deviceUuid(deviceUuid)
                        .deviceInfo("acer315-55kg")
                .isAvailable(true)
                .build());

        userId = userRepository.save(user).getId();
    }

    @Test
    void shouldFind_byDeviceUuidAndUserId() {
        final Optional<Device> foundDevice = deviceRepository.findByDeviceUuidAndUserId(deviceUuid, userId);

        assertThat(foundDevice.isPresent()).isTrue();
        foundDevice.ifPresent(d -> assertThat(d.getDeviceInfo())
                .isEqualTo("acer315-55kg"));
    }
}

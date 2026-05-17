package com.Ali.Store.App.refreshToken;

import com.Ali.Store.App.entities.userAndProfileUser.Device;
import com.Ali.Store.App.entities.userAndProfileUser.RefreshToken;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.exceptions.user.NotFoundDevice;
import com.Ali.Store.App.exceptions.user.NotFoundUser;
import com.Ali.Store.App.repository.RefreshTokenRepository;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static java.time.Duration.ofMinutes;
import static java.time.Instant.now;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class RefreshTokenRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenRepository repository;

    private Users user;
    private Device device;
    private UUID deviceUuid;
    private UUID tokenUuid;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        deviceUuid = randomUUID();
        tokenUuid = randomUUID();

        user = Users.builder()
                .username("09876543210")
                .createdAt(LocalDateTime.now())
                .status(true)
                .build();
        device = Device.builder()
                .deviceUuid(deviceUuid)
                .isAvailable(true)
                .build();
        user.addDevice(device);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenUuid)
                .expiryDate(now().plus(ofMinutes(10)))
                .build();

        device.addRefreshToken(refreshToken);

        userRepository.save(user);
    }

    @Test
    void shouldFindByDeviceUuid_whenDeviceAndRefreshTokenExist() {
        final Optional<RefreshToken> foundRefreshToken = repository.findByDeviceUUid(deviceUuid);

        foundRefreshToken.ifPresent(rt ->  assertThat(rt)
                .extracting(RefreshToken::getToken)
                .isEqualTo(tokenUuid));

    }

    @Test
    @Transactional(readOnly = true)
    void shouldDelete_byUserIdAndDeviceUuid_whenExists() {
        final Users user = getUser();
        final UUID deviceUuid = getDeviceUuid(user);

        assertThat(repository.findAll()).isNotEmpty();

        repository.deleteByUserIdAndDeviceUuid(user.getId(), deviceUuid);

        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void shouldFind_byTokenAndDeviceAndUser() {
        final Optional<RefreshToken> foundToken = repository.findByTokenAndDeviceUuid(tokenUuid, deviceUuid);

        assertThat(foundToken.isPresent()).isTrue();
        foundToken.ifPresent(rt -> assertThat(rt)
                .extracting(RefreshToken::getToken)
                .isEqualTo(tokenUuid));
    }

    private UUID getDeviceUuid(Users user) {
        return user.getDevices().stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundDevice(device.getDeviceUuid()))
                .getDeviceUuid();
    }

    private Users getUser() {
        return userRepository.findByUsername(this.user.getUsername())
                .orElseThrow(() -> new NotFoundUser(this.user.getUsername()));
    }
}

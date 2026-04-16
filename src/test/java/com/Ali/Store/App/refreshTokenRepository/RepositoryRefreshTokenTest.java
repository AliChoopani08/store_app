package com.Ali.Store.App.refreshTokenRepository;

import com.Ali.Store.App.entities.userAndProfileUser.RefreshToken;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.RefreshTokenRepository;
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
public class RepositoryRefreshTokenTest {

    @Autowired
    private RefreshTokenRepository repositoryRefreshToken;
    @Autowired
    private UserRepository repositoryUser;

    private Users user;
    private String deviceId;

    @BeforeEach
    void setUp() {
        final Users user = Users.builder()
                .username("091234567890")
                .createdAt(now())
                .build();

        deviceId = "fake.device.id";
        final RefreshToken refreshToken = RefreshToken.builder()
                .deviceId(deviceId)
                .token("fake.refresh.token")
                .build();

        user.addRefreshToken(refreshToken);

        this.user = repositoryUser.save(user);
    }

    @Test
    void shouldFindRefreshToken_byUserIdAndDeviceId_whenUserAndRefreshTokenExist() {
        final Long userId = user.getId();
        final String username = user.getUsername();

        final Optional<RefreshToken> refreshToken = repositoryRefreshToken.findByUserIdAndDeviceId(userId, deviceId);

        refreshToken.ifPresent(token ->
                assertThat(token)
                        .extracting(RefreshToken::getDeviceId, t -> t.getUser().getUsername())
                        .containsExactly(deviceId, username));
    }
}

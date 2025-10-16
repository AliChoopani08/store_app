package com.Ali.Store.App.user.repository;

import com.Ali.Store.App.entities.userAndProfileUser.RefreshToken;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryRefreshToken;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryUser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;

import static java.time.LocalDateTime.now;


@DataJpaTest
public class RepositoryRefreshTokenShould {

    @Autowired
    private RepositoryRefreshToken repositoryRefreshToken;
    @Autowired
    private RepositoryUser repositoryUser;

    @Test
    void find_refreshToken_by_user_and_deviceId() {
        final Users user = new Users();
        user.setUsername("09112223344");
        user.setPassword("Password123");
        user.setCreatedAt(now());

        final RefreshToken refreshToken = new RefreshToken();
        refreshToken.setDeviceId("acer315-55kg");
        refreshToken.setDeviceInfo("windows 10 core-i3");
        final String fakeToken = "JisjhdjjdbcncjdhfyeHSHDU763hege";
        refreshToken.setToken(fakeToken);

        user.addRefreshToken(refreshToken);

        repositoryUser.save(user);
        final Optional<RefreshToken> byUserAndDeviceId = repositoryRefreshToken.findByUserIdAndDeviceId(1L, "acer315-55kg");

        Assertions.assertThat(byUserAndDeviceId.get())
                .extracting(RefreshToken::getDeviceId, RefreshToken::getDeviceInfo, RefreshToken::getUser)
                .containsExactly("acer315-55kg", "windows 10 core-i3", user);
    }
}

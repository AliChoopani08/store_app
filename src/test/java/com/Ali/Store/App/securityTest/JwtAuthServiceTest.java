package com.Ali.Store.App.securityTest;

import com.Ali.Store.App.security.jwt.JwtAuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThat;


public class JwtAuthServiceTest {

    @Autowired
    private JwtAuthServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtAuthServiceImpl();
    }

    @Test
    void shouldGenerateAccessToken_byUsername() {
        final String generatedToken = jwtService.generateAccessToken("chopaniali373@gmail.com");

        assertThat(generatedToken).isInstanceOf(String.class);
    }

    @Test
    void shouldGenerateKeyWithHMACAlgorithm_bySecretKey() {
        final String secretKey = "fake=secret=key=at=env/itIsMySecretKey/123456789=";

        final SecretKey key = jwtService.getKey(secretKey);

        assertThat(key).isNotNull();
    }

    @Test
    void shouldExtractUsernameInToken_whenSecretKeyBeValid() {
        final String token = jwtService.generateAccessToken("chopaniali373@gmail.com");

        final String extractedUsername = jwtService.extractUsername(token);

        assertThat(extractedUsername).isEqualTo("chopaniali373@gmail.com");
    }

    @Test
    void shouldCheckTokenCredit_whenUsernameBeValidAndTokenNotBeExpired() {
        final String username = "09330825474";
        final String token = jwtService.generateAccessToken(username);

        final boolean isTokenValid = jwtService.isAuthTokenValid(username, token);

        assertThat(isTokenValid).isTrue();
    }
}

package com.Ali.Store.App.securityTest;

import com.Ali.Store.App.exceptions.security.JwtPasswordExpiredException;
import com.Ali.Store.App.security.jwt.JwtPwdVerifyServiceImpl;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static java.lang.System.getenv;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JwtPasswordVerifyTest {

    @Autowired
    private JwtPwdVerifyServiceImpl jwtService;

    public static final String USERNAME = "09876543210";

    @BeforeEach
    void setUp() {
        jwtService = new JwtPwdVerifyServiceImpl();
    }

    @Test
    void shouldGeneratePasswordVerifyToken_byUsername() {
        final String createdToken = jwtService.generatePwdVerificationToken(USERNAME);

        assertThat(createdToken).isInstanceOf(String.class);
    }

    @Test
    void shouldCheckTokenCredit_whenTokenNotBeExpired() {
        final String createdToken = jwtService.generatePwdVerificationToken(USERNAME);

        final boolean isTokenValid = jwtService.isPwdVerifyTokenValid(createdToken);

        assertThat(isTokenValid).isTrue();
    }

    @Test
    void shouldThrowException_whenTokenExpired() {
        final String secret_key = getenv("SECRET_KEY_JWT_PASSWORD_VERIFICATION");

        final String createdExpiredToken = Jwts.builder()
                .subject(USERNAME)
                .issuedAt(Date.from(Instant.now().minus(Duration.ofHours(5))))
                .expiration(Date.from(Instant.now().minus(Duration.ofHours(1))))
                .signWith(jwtService.getKey(secret_key))
                .compact();

        assertThatThrownBy(() -> jwtService.isPwdVerifyTokenValid(createdExpiredToken))
                .isInstanceOf(JwtPasswordExpiredException.class)
                .hasMessageContaining("This jwt password token has expired !");
    }
}

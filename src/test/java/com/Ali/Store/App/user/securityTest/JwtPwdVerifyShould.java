package com.Ali.Store.App.user.securityTest;

import com.Ali.Store.App.exceptions.security.PasswordVerifyTokenExceptions;
import com.Ali.Store.App.security.jwt.JwtPwdVerifyServiceImpl;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JwtPwdVerifyShould {
    @Autowired
    private JwtPwdVerifyServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtPwdVerifyServiceImpl();
    }

    @Test
    void generate_password_verify_token() {
        final String createdToken = jwtService.generatePwdVerificationToken("09213456567");

        assertThat(createdToken).isInstanceOf(String.class);
    }

    @Test
    void confirm_validation_password_verify_token() {
        final String createdToken = jwtService.generatePwdVerificationToken("09345635425");

        final boolean isTokenValid = jwtService.isPwdVerifyTokenValid(createdToken);

        assertThat(isTokenValid).isTrue();
    }

    @Test
    void throw_exception_when_password_verify_token_is_expired() {
        final String secret_key = System.getenv("SECRET_KEY_JWT_PASSWORD_VERIFICATION");

        final String createdExpiredToken = Jwts.builder()
                .subject("choopani12_ali@gmail.com")
                .issuedAt(Date.from(Instant.now().minus(Duration.ofHours(5))))
                .expiration(Date.from(Instant.now().minus(Duration.ofHours(1))))
                .signWith(jwtService.getKey(secret_key))
                .compact();

        assertThatThrownBy(() -> jwtService.isPwdVerifyTokenValid(createdExpiredToken))
                .isInstanceOf(PasswordVerifyTokenExceptions.class)
                .hasMessageContaining("This password verify token expired !");
    }
}

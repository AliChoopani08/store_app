package com.Ali.Store.App.user.securityTest;

import com.Ali.Store.App.security.jwt.JwtAuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class JwtAuthServiceShould {

    @Autowired
    private JwtAuthServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtAuthServiceImpl();
    }

    @Test
    void generate_access_token() {
        final String generatedToken = jwtService.generateAccessToken("chopaniali373@gmail.com");

        assertThat(generatedToken).isInstanceOf(String.class);
    }

    @Test
    void test_get_key_is_correct() {
        final String getEnv = System.getenv("SECRET-KEY-JWT");
        final SecretKey key = jwtService.getKey(getEnv);

        assertThat(key).isNotNull();
    }

    @Test
    void extract_username() {
        final String token = jwtService.generateAccessToken("chopaniali373@gmail.com");

        final String extractedUsername = jwtService.extractUsername(token);

        assertThat(extractedUsername).isEqualTo("chopaniali373@gmail.com");
    }

    @Test
    void confirm_validation_access_token() {
        final String username = "09330825474";
        final String token = jwtService.generateAccessToken(username);

        final boolean isTokenValid = jwtService.isAuthTokenValid(username, token);

        assertThat(isTokenValid).isTrue();
    }
}

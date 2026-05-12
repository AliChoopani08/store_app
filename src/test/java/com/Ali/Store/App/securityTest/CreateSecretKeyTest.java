package com.Ali.Store.App.securityTest;

import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateSecretKeyTest {

    @Test
    void shouldGenerateASecretKeyWithBase64() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);

        final String generatedSecretKey = Encoders.BASE64.encode(bytes);

        assertThat(generatedSecretKey)
                .isNotEmpty()
                .isInstanceOf(String.class);
    }
}

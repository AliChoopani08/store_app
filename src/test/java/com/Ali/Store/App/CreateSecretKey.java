package com.Ali.Store.App;

import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

public class CreateSecretKey {

    @Test
    void create_secret_key() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);

        final String generatedSecret_key = Encoders.BASE64.encode(bytes);

        System.out.println(generatedSecret_key);
    }
}

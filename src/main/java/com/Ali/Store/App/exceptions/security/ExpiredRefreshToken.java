package com.Ali.Store.App.exceptions.security;

import java.util.UUID;

public class ExpiredRefreshToken extends RuntimeException {
    public ExpiredRefreshToken(UUID refreshTokenId) {
        super("This refresh [" + refreshTokenId + "] token is expired !");
    }
}

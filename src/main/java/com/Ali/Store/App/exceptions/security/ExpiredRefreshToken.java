package com.Ali.Store.App.exceptions.security;

import java.util.UUID;

import static java.lang.String.format;

public class ExpiredRefreshToken extends RuntimeException {
    public ExpiredRefreshToken(UUID refreshTokenId) {
        super(format("This refresh [%s] token is expired !", refreshTokenId));
    }
}

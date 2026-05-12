package com.Ali.Store.App.exceptions.security;

public class ExpiredRefreshToken extends RuntimeException {
    public ExpiredRefreshToken(String refreshTokenId) {
        super("This refresh [" + refreshTokenId + "] token is expired !");
    }
}

package com.Ali.Store.App.exceptions.security;

public class NotFoundRefreshToken extends RuntimeException {
    public NotFoundRefreshToken() {
        super("This refresh token is not exist in database !");
    }
}

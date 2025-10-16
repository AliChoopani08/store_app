package com.Ali.Store.App.exceptions.security;

public class NotFoundRefreshToken extends RuntimeException {
    public NotFoundRefreshToken(String message) {
        super(message);
    }
}

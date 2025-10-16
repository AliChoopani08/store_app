package com.Ali.Store.App.exceptions.security;

public class ExpiredRefreshToken extends RuntimeException {
    public ExpiredRefreshToken(String message) {
        super(message);
    }
}

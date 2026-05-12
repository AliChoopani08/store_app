package com.Ali.Store.App.exceptions.user;

public class DuplicateRefreshToken extends RuntimeException {
    public DuplicateRefreshToken() {
        super("This Refresh Token is already active in database. Please generate a new Access Token with this refresh token.");
    }
}

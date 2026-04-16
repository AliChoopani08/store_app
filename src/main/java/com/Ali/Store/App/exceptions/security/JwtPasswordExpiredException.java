package com.Ali.Store.App.exceptions.security;

public class JwtPasswordExpiredException extends RuntimeException {
    public JwtPasswordExpiredException() {
        super("This jwt password token has expired !");
    }
}

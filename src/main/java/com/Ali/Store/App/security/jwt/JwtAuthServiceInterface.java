package com.Ali.Store.App.security.jwt;

public interface JwtAuthServiceInterface {
    String generateAccessToken(String username);
    boolean isAuthTokenValid(String username, String token);
    String extractUsername(String token);
    boolean isAuthTokenExpired(String token);
}

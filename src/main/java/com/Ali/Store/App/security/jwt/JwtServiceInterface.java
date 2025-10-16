package com.Ali.Store.App.security.jwt;

public interface JwtServiceInterface {

    String generateToken(String username);
    boolean isTokenValid(String username, String token);
    String extractUsername(String token);
    boolean isTokenExpired(String token);
}

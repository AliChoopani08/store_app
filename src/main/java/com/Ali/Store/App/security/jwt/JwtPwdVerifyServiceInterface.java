package com.Ali.Store.App.security.jwt;

public interface JwtPwdVerifyServiceInterface {
    String generatePwdVerificationToken(String username);
    boolean isPwdVerifyTokenValid(String token);
    String extractUsername(String token);
}

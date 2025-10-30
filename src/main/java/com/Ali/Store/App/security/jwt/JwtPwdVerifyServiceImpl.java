package com.Ali.Store.App.security.jwt;

import com.Ali.Store.App.exceptions.security.PasswordVerifyTokenExceptions;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtPwdVerifyServiceImpl implements JwtPwdVerifyServiceInterface{

    private final static String SECRET_KEY = System.getenv("SECRET_KEY_JWT_PASSWORD_VERIFICATION");
    private final Duration pwdVerifyTokenExpiryDuration = Duration.ofMinutes(5);



    @Override
    public String generatePwdVerificationToken(String username) {
        Instant expiryToken = Instant.now().plus(pwdVerifyTokenExpiryDuration);

        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(Instant.now()))
                .claim("tokenType", "PASSWORD_VERIFICATION")
                .expiration(Date.from(expiryToken))
                .signWith(getKey(SECRET_KEY))
                .compact();

    }

    public SecretKey getKey(String secretKey) {
        if (SECRET_KEY.isEmpty()) {
            throw new IllegalStateException("Environment value = " + secretKey + " is empty or not exists !");
        }
        final byte[] decodeSecretKey = Decoders.BASE64.decode(secretKey);

        return Keys.hmacShaKeyFor(decodeSecretKey);
    }

    @Override
    public boolean isPwdVerifyTokenValid(String token) {
            final Date expiration = Jwts.parser()
                    .verifyWith(getKey(SECRET_KEY))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();

            return expiration.after(new Date());
    }

    @Override
    public String extractUsername(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKey(SECRET_KEY))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        }
        catch (MalformedJwtException ex) {
            throw new PasswordVerifyTokenExceptions("The Password Verify Token has invalid format !");
        }
        catch (SignatureException ex) {
            throw new PasswordVerifyTokenExceptions("The Password Verify Token signature is invalid");
        }
        catch (ExpiredJwtException ex) {
            throw new PasswordVerifyTokenExceptions("This password verify token expired !");
        }
    }
}

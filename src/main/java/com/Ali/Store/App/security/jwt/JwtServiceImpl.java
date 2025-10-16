package com.Ali.Store.App.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;


@Service
public class JwtServiceImpl implements JwtServiceInterface{

    private final static String SECRET_KEY = System.getenv("SECRET-KEY-JWT");
    final Duration duration = Duration.ofMinutes(15); // 15 minutes



    @Override
    public String generateToken(String username) {
         Instant expiryDate = Instant.now().plus(duration);

        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(expiryDate))
                .signWith(getKey(SECRET_KEY))
                .compact();
    }

    public SecretKey getKey(String secret_key) {
        if (secret_key == null || secret_key.isEmpty()) {
            throw new IllegalStateException("Environment value = " + secret_key + " is empty or not exists !");
        }
         byte[] bytesKey = Decoders.BASE64.decode(secret_key); // convert to bite for security and signature

        return Keys.hmacShaKeyFor(bytesKey); // create signed key with HMAC algorithm (HS256)
    }

    @Override
    public boolean isTokenValid(String username, String token){
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    @Override
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getKey(SECRET_KEY))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    @Override
    public boolean isTokenExpired(String token) {
            final Date expiration = Jwts.parser()
                    .verifyWith(getKey(SECRET_KEY))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();

            return expiration.before(new Date());
    }
}

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
public class JwtAuthServiceImpl implements JwtAuthServiceInterface {

    private final String SECRET_KEY;

    public JwtAuthServiceImpl() {
        this.SECRET_KEY=System.getenv("SECRET_KEY");
        if (this.SECRET_KEY == null || this.SECRET_KEY.isEmpty()) {
            throw new IllegalStateException("Environment variable is empty or not exists !");
        }
    }

    final Duration authTokenExpiryDuration = Duration.ofMinutes(15); // 15 minutes



    @Override
    public String generateAccessToken(String username) {
         Instant expiryDate = Instant.now().plus(authTokenExpiryDuration);

        return Jwts.builder()
                .subject(username)
                .claim("tokenType", "ACCESS_TOKEN")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(expiryDate))
                .signWith(getKey(SECRET_KEY))
                .compact();
    }

    public SecretKey getKey(String secret_key) {
         byte[] bytesKey = Decoders.BASE64.decode(secret_key); // convert to byte for security and signature

        return Keys.hmacShaKeyFor(bytesKey); // create signed key with HMAC algorithm (HS256)
    }

    @Override
    public boolean isAuthTokenValid(String username, String token){
        return extractUsername(token).equals(username) && !isAuthTokenExpired(token);
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
    public boolean isAuthTokenExpired(String token) {
            final Date expiration = Jwts.parser()
                    .verifyWith(getKey(SECRET_KEY))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();

            return expiration.before(new Date());
    }
}

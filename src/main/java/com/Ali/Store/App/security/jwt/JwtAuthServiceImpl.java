package com.Ali.Store.App.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;


@Service
@Slf4j
public class JwtAuthServiceImpl implements JwtAuthServiceInterface {

    private static final String AUTH_SECRET_KEY_NAME = "SECRET_KEY_ACCESS_TOKEN";
    private final String SECRET_KEY;

    public JwtAuthServiceImpl() {
        this.SECRET_KEY=System.getenv(AUTH_SECRET_KEY_NAME);
        if (this.SECRET_KEY == null || this.SECRET_KEY.isEmpty()) {
            log.error("Environment variable [{}] is empty or doesn't exist !", AUTH_SECRET_KEY_NAME);
            throw new IllegalStateException("Environment variable is empty or doesn't exist !");
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

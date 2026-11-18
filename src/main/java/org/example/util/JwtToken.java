package org.example.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtToken {
    private final SecretKey key;
    private final long expirationMs;

    public JwtToken(@Value("${jwt.secret.key}") String secret,
                     @Value("${jwt.expiration.ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(long userId){
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    public Long extractUserId(String authHeader){
        if(authHeader == null || authHeader.isBlank()){
            throw new IllegalArgumentException("Missing Token");
        }
        String token =authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        try{
            String subject = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return Long.valueOf(subject);
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid or expired token");
        }
    }
}

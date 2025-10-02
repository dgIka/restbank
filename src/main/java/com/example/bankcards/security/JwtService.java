package com.example.bankcards.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final Key key;
    private final long ttlMillis;

    public JwtService(@Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.ttl-seconds:3600}") long ttlSeconds) {
        byte[] raw = secret.getBytes(StandardCharsets.UTF_8);
        if (raw.length < 32) {
            throw new IllegalArgumentException("Invalid JWT secret");
        }

        this.key = Keys.hmacShaKeyFor(raw);
        this.ttlMillis = ttlSeconds * 1000;
    }

    public String generate(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(new Date(now.toEpochMilli() + ttlMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public String getSubject(String token) {
        return parse(token).getBody().getSubject();
    }
}

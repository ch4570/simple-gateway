package com.example.userservice.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final Environment env;


    public String generateToken(String email) {
        long tokenPeriod = 1000L * 60L * 60L * 24L * 365L; // 1년
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenPeriod))
                .signWith(SignatureAlgorithm.HS256, getSecretKey())
                .compact();
    }

    private final String getSecretKey() {
        return Base64.getEncoder().encodeToString(env.getProperty("jwt.secret").getBytes());
    }
}

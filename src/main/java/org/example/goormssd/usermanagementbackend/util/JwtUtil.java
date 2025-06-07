package org.example.goormssd.usermanagementbackend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.access-token-expiration-ms:3600000}") // 기본 1시간
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration-ms:604800000}") // 기본 7일
    private long refreshTokenExpiration;

    private SecretKey secretKey;

    @PostConstruct
    protected void init() {
        // Base64 인코딩 후 Key 객체로 변환
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 생성 (사용자 이메일, 만료 시간, 토큰 타입)
    public String generateToken(String email, long expirationMs) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessToken(String email) {
        return generateToken(email, accessTokenExpiration);
    }

    // RefreshToken 생성 (7일)
    public String generateRefreshToken(String email) {
        return generateToken(email, refreshTokenExpiration);
    }


    // 이메일 추출
    public String extractEmail(String token) {
        return parseClaims(token)
                .getBody()
                .getSubject();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("[JWT 검증 실패] 원인: {}", e.getMessage());
            return false;
        }
    }

    private Jws<Claims> parseClaims(String token) {
        String rawToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(rawToken);
    }
}


package org.example.goormssd.usermanagementbackend.config.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String secretKeyString;

    private SecretKey secretKey;

//    @PostConstruct
//    protected void init() {
//        // Base64 인코딩 후 Key 객체로 변환
//        byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
//        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
//    }
    @PostConstruct
    protected void init() {
        if (secretKeyString == null || secretKeyString.isBlank()) {
            throw new IllegalStateException("JWT_SECRET_KEY 환경변수가 설정되지 않았습니다.");
        }
        try {
            byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
            this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            log.error("JWT 시크릿 키 디코딩 실패: {}", e.getMessage());
            throw new IllegalStateException("유효하지 않은 JWT 시크릿 키입니다.", e);
        }
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
        // 1시간
        long accessTokenExpiration = 3600000L;
        return generateToken(email, accessTokenExpiration);
    }

    // RefreshToken 생성 (7일)
    public String generateRefreshToken(String email) {
        // 7일
        long refreshTokenExpiration = 604800000L;
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

    public String generateReauthToken(UserDetails userDetails) {
        Map<String,Object> claims = new HashMap<>();
        claims.put("reauth", true);
        return createToken(claims, userDetails.getUsername(),
                Duration.ofMinutes(5)); // 5분 유효
    }

    public String createToken(Map<String, Object> claims, String subject, Duration validity) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validity.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}


package org.example.goormssd.usermanagementbackend.util;

import org.springframework.stereotype.Component;

@Component
public class TokenProvider {

    private final JwtUtil jwtUtil;

    public TokenProvider(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String generateAccessToken(String email) {
        return jwtUtil.generateToken(email);
    }

    public String generateRefreshToken(String email) {
        // Implement refresh token generation logic
        return jwtUtil.generateToken(email); // Placeholder
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    public String extractEmail(String token) {
        return jwtUtil.extractEmail(token);
    }
}
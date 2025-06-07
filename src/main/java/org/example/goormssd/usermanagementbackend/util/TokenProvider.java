//package org.example.goormssd.usermanagementbackend.util;
//
//import org.springframework.stereotype.Component;
//
//@Component
//public class TokenProvider {
//
//    private final JwtUtil jwtUtil;
//
//    public TokenProvider(JwtUtil jwtUtil) {
//        this.jwtUtil = jwtUtil;
//    }
//
//    // AccessToken은 일반적인 인증에 사용 (1시간)
//    public String generateAccessToken(String email) {
//        return jwtUtil.generateAccessToken(email);
//    }
//
//    // RefreshToken은 장기 세션 유지에 사용 (7일)
//    public String generateRefreshToken(String email) {
//        return jwtUtil.generateRefreshToken(email);
//    }
//
//    // 토큰 유효성 검사
//    public boolean validateToken(String token) {
//        return jwtUtil.validateToken(token);
//    }
//
//    // 토큰에서 이메일(subject) 추출
//    public String extractEmail(String token) {
//        return jwtUtil.extractEmail(token);
//    }
//}

// TokenProvider.java 삭제
// JwtUtil.java로 통합
// 기능이 추가되지 않고 단순 중계만 하는 경우로 보여서
// JwtUtil에서 직접 사용하는 것이 더 간결하고 명확함
// 향후 기능 확장이 상정되어 있다면 유지하는 것도 고려대상으로 보임
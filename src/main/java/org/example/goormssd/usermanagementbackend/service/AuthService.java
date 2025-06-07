package org.example.goormssd.usermanagementbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.domain.Member;
import org.example.goormssd.usermanagementbackend.domain.Token;
import org.example.goormssd.usermanagementbackend.dto.request.LoginRequestDto;
import org.example.goormssd.usermanagementbackend.dto.response.LoginUserDto;
import org.example.goormssd.usermanagementbackend.repository.MemberRepository;
import org.example.goormssd.usermanagementbackend.repository.TokenRepository;
import org.example.goormssd.usermanagementbackend.service.dto.LoginResult;
import org.example.goormssd.usermanagementbackend.util.JwtUtil;
//import org.example.goormssd.usermanagementbackend.util.TokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    // TokenProvider 삭제 검토
//    private final TokenProvider tokenProvider;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // AuthController에서와 동일한 이유로 생성자 주입 방식 사용
//    @Autowired
//    public AuthService(MemberRepository memberRepository,
//                       TokenRepository tokenRepository,
//                       TokenProvider tokenProvider,
//                       PasswordEncoder passwordEncoder) {
//        this.memberRepository = memberRepository;
//        this.tokenRepository = tokenRepository;
//        this.tokenProvider = tokenProvider;
//        this.passwordEncoder = passwordEncoder;
//    }

    // service layer에서 로그인 결과를 DTO로 반환하는 방식으로 변경
    // service/dto/LoginResult.java
    // 단일 책임 원칙(SRP) -> 서비스는 비즈니스 로직만 처리하고, DTO는 데이터 전송을 담당하도록 분리
    // 구조적, 협업적, 유지보수적 측면에서 훨씬 바람직
//    public class LoginResult {
//        private String accessToken;
//        private String refreshToken;
//        private LoginUserDto user;
//
//        public LoginResult(String accessToken, String refreshToken, LoginUserDto user) {
//            this.accessToken = accessToken;
//            this.refreshToken = refreshToken;
//            this.user = user;
//        }
//
//        public String getAccessToken() {
//            return accessToken;
//        }
//
//        public String getRefreshToken() {
//            return refreshToken;
//        }
//
//        public LoginUserDto getUser() {
//            return user;
//        }
//    }

    public LoginResult loginWithUserInfo(LoginRequestDto loginRequest) {
        // Token 필드명 member 반영
        Member member = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new RuntimeException("비밀번호가 올바르지 않습니다.");
        }

        // AccessToken은 클라이언트가 저장 (서버 저장 불필요)
        // DB에는 RefreshToken만 저장 (보안 이슈 최소화)
        // 아직 명확한 해답을 찾지 못했음..
        // 일단 코드적으로 AccessToken은 저장하지 않는 로직을 변경
        String accessToken = jwtUtil.generateAccessToken(member.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(member.getEmail());

//        Token token = new Token();
//        token.setUser(user);
////        token.setAccessToken(accessToken);
//        token.setRefreshToken(refreshToken);
//        token.setDeletedAt(null);

        // RefreshToken 엔티티 저장 (Soft Delete 시 deletedAt 업데이트)
        // build() 메서드를 사용하여 Token 객체 생성
        Token token = Token.builder()
                .member(member)                 // 회원 정보 매핑
                .refreshToken(refreshToken)     // 저장할 리프레시 토큰
                .deletedAt(null)                // 초기에는 미삭제
                .build();
        tokenRepository.save(token);

        return new LoginResult(
                accessToken,
                refreshToken,
                new LoginUserDto(member)
        );
    }

//    public void logout(String accessToken) {
//        Token token = tokenRepository.findByAccessToken(accessToken)
//                .orElseThrow(() -> new RuntimeException("토큰을 찾을 수 없습니다."));
//        token.setDeletedAt(LocalDateTime.now());
//        tokenRepository.save(token);
//    }

    // 로그아웃
    // 전달받은 RefreshToken으로 DB에서 엔티티 조회
    // 존재 시 deletedAt 업데이트로 소프트 삭제
    public void logout(String refreshToken) {
        // 아직 삭제되지 않은(refreshToken + deletedAt = null) 토큰을 DB에서 조회
        tokenRepository.findByRefreshTokenAndDeletedAtIsNull(refreshToken)
                .ifPresent(token -> {
                    token.setDeletedAt(LocalDateTime.now());
                    tokenRepository.save(token);
                });
    }
}

package org.example.goormssd.usermanagementbackend.service;

import org.example.goormssd.usermanagementbackend.domain.Member;
import org.example.goormssd.usermanagementbackend.domain.Token;
import org.example.goormssd.usermanagementbackend.dto.request.LoginRequestDto;
import org.example.goormssd.usermanagementbackend.dto.response.LoginUserDto;
import org.example.goormssd.usermanagementbackend.repository.MemberRepository;
import org.example.goormssd.usermanagementbackend.repository.TokenRepository;
import org.example.goormssd.usermanagementbackend.util.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(MemberRepository memberRepository,
                       TokenRepository tokenRepository,
                       TokenProvider tokenProvider,
                       PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.tokenRepository = tokenRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public class LoginResult {
        private String accessToken;
        private String refreshToken;
        private LoginUserDto user;

        public LoginResult(String accessToken, String refreshToken, LoginUserDto user) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.user = user;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public LoginUserDto getUser() {
            return user;
        }
    }

    public LoginResult loginWithUserInfo(LoginRequestDto loginRequest) {
        Member user = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 올바르지 않습니다.");
        }

        String accessToken = tokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());

        Token token = new Token();
        token.setUser(user);
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setDeletedAt(null);

        tokenRepository.save(token);

        return new LoginResult(accessToken, refreshToken, new LoginUserDto(user));
    }

    public void logout(String accessToken) {
        Token token = tokenRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new RuntimeException("토큰을 찾을 수 없습니다."));
        token.setDeletedAt(LocalDateTime.now());
        tokenRepository.save(token);
    }
}

package org.example.goormssd.usermanagementbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.domain.Member;
import org.example.goormssd.usermanagementbackend.domain.Token;
import org.example.goormssd.usermanagementbackend.dto.request.*;
import org.example.goormssd.usermanagementbackend.dto.response.LoginUserDto;
import org.example.goormssd.usermanagementbackend.repository.MemberRepository;
import org.example.goormssd.usermanagementbackend.repository.TokenRepository;
import org.example.goormssd.usermanagementbackend.util.TokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final PhoneVerificationService phoneVerificationService;
    private final EmailService emailService;

    public class LoginResult {
        private final String accessToken;
        private final String refreshToken;
        private final LoginUserDto user;

        public LoginResult(String accessToken, String refreshToken, LoginUserDto user) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.user = user;
        }

        public String getAccessToken() { return accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public LoginUserDto getUser() { return user; }
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

    public void verifyPhoneCode(PhoneVerifyCodeRequestDto dto) {
        phoneVerificationService.verifyCode(dto.getPhoneNumber(), dto.getCode());
    }

    public String findEmailByUsernameAndPhone(FindEmailRequestDto dto) {
//        phoneVerificationService.verifyCode(dto.getPhoneNumber(), dto.getCode());

        return memberRepository.findAll().stream()
                .filter(m -> m.getUsername().equals(dto.getUsername())
                        && m.getPhoneNumber().equals(dto.getPhoneNumber()))
                .map(Member::getEmail)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));
    }

    public void resetPasswordAndSendEmail(FindPasswordRequestDto dto) {
        Member member = memberRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!member.getUsername().equals(dto.getUsername())) {
            throw new RuntimeException("사용자 이름이 일치하지 않습니다.");
        }

        String tempPassword = generateTempPassword();
        member.setPassword(passwordEncoder.encode(tempPassword));
        memberRepository.save(member);

        emailService.sendVerificationEmail(member.getEmail(), tempPassword);
    }

    private String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }


}
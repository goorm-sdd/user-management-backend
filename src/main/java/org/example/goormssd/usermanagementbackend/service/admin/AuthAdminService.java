package org.example.goormssd.usermanagementbackend.service.admin;

import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.domain.Member;
import org.example.goormssd.usermanagementbackend.domain.Token;
import org.example.goormssd.usermanagementbackend.dto.auth.requset.LoginRequestDto;
import org.example.goormssd.usermanagementbackend.dto.auth.response.LoginResult;
import org.example.goormssd.usermanagementbackend.dto.auth.response.LoginUserDto;
import org.example.goormssd.usermanagementbackend.exception.ErrorCode;
import org.example.goormssd.usermanagementbackend.exception.GlobalException;
import org.example.goormssd.usermanagementbackend.repository.MemberRepository;
import org.example.goormssd.usermanagementbackend.repository.TokenRepository;
import org.example.goormssd.usermanagementbackend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthAdminService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public LoginResult loginWithUserInfo(LoginRequestDto loginRequest) {
        Member member = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));


        if (member.getRole() != Member.Role.ADMIN) {
            throw new GlobalException(ErrorCode.NOT_ADMIN);
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new GlobalException(ErrorCode.WRONG_PASSWORD);
        }

//        if (!member.isEmailVerified()) {
//            throw new ResponseStatusException(
//                    HttpStatus.FORBIDDEN, "이메일 인증을 완료해야 로그인할 수 있습니다."
//            );
//        }

        String accessToken = jwtUtil.generateAccessToken(member.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(member.getEmail());


        Token token = Token.builder()
                .member(member)
                .refreshToken(refreshToken)
                .deletedAt(null)
                .build();
        tokenRepository.save(token);

        return new LoginResult(
                accessToken,
                refreshToken,
                new LoginUserDto(member)
        );
    }

    public void logout(String refreshToken) {
        tokenRepository.findByRefreshTokenAndDeletedAtIsNull(refreshToken)
                .ifPresent(token -> {
                    token.setDeletedAt(LocalDateTime.now());
                    tokenRepository.save(token);
                });
    }
}

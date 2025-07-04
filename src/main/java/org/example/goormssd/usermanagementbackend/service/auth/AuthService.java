package org.example.goormssd.usermanagementbackend.service.auth;

import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.domain.Member;
import org.example.goormssd.usermanagementbackend.domain.Token;
import org.example.goormssd.usermanagementbackend.dto.auth.requset.FindEmailRequestDto;
import org.example.goormssd.usermanagementbackend.dto.auth.requset.FindPasswordRequestDto;
import org.example.goormssd.usermanagementbackend.dto.auth.requset.LoginRequestDto;
import org.example.goormssd.usermanagementbackend.dto.auth.requset.SignupRequestDto;
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
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    // TokenProvider 삭제 검토
//    private final TokenProvider tokenProvider;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final PhoneVerificationService phoneVerificationService;
    private final EmailVerificationService emailVerificationService;
    private final EmailService emailService;

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

    public void signup(SignupRequestDto requestDto) {
        if (!requestDto.getPassword().equals(requestDto.getPasswordCheck())) {
            throw new GlobalException(ErrorCode.PASSWORDS_DO_NOT_MATCH);
        }

        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new GlobalException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        phoneVerificationService.verifyCode(requestDto.getPhoneNumber(), requestDto.getCode());
        phoneVerificationService.deleteCode(requestDto.getPhoneNumber());


        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 회원 생성
        Member member = Member.builder()
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .phoneNumber(requestDto.getPhoneNumber())
                .emailVerified(false)
                .role(Member.Role.USER)
                .status(Member.Status.ACTIVE)
                .build();

        // 저장
        memberRepository.save(member);

        // 인증 코드 생성 및 메일 전송
        String code = emailVerificationService.createVerificationEntry(member);
        emailService.sendVerificationEmail(member.getEmail(), code);

    }

    public boolean isEmailDuplicate(String email) {
        return memberRepository.existsByEmail(email);
    }

    public LoginResult loginWithUserInfo(LoginRequestDto loginRequest) {
        // Token 필드명 member 반영
        Member member = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new GlobalException(ErrorCode.WRONG_PASSWORD);
        }

        if (!member.isEmailVerified()) {
            throw new GlobalException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        if (member.getStatus() == Member.Status.DELETED) {
            throw new GlobalException(ErrorCode.MEMBER_DELETED);
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

    //전달된 refreshToken이 DB에 남아있고 deletedAt이 null인지 확인
    public boolean isValidRefreshToken(String refreshToken) {
        return tokenRepository
                .findByRefreshTokenAndDeletedAtIsNull(refreshToken)
//                .map(token -> token.getMember())                // 토큰 소유자(Member) 조회
//                .filter(member -> member.isEmailVerified())    // 이메일 인증 여부 체크
//                .filter(member -> member.getStatus().isActive()) // 계정 상태(ACTIVE) 체크
                .isPresent();
    }

//    public void verifyPhoneCode(PhoneVerifyCodeRequestDto dto) {
//        phoneVerificationService.verifyCode(dto.getPhoneNumber(), dto.getCode());
//    }

    public String findEmailByUsernameAndPhone(FindEmailRequestDto dto) {
        phoneVerificationService.verifyCode(dto.getPhoneNumber(), dto.getCode());
        phoneVerificationService.deleteCode(dto.getPhoneNumber());
        return memberRepository.findAll().stream()
                .filter(m -> m.getUsername().equals(dto.getUsername())
                        && m.getPhoneNumber().equals(dto.getPhoneNumber()))
                .map(Member::getEmail)
                .findFirst()
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
    }

    public void resetPasswordAndSendEmail(FindPasswordRequestDto dto) {
        Member member = memberRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new GlobalException(ErrorCode.EMAIL_NOT_FOUND));

        if (!member.getUsername().equals(dto.getUsername())) {
            throw new GlobalException(ErrorCode.USERNAME_NOT_MATCH);
        }

        phoneVerificationService.verifyCode(member.getPhoneNumber(), dto.getCode());
        phoneVerificationService.deleteCode(member.getPhoneNumber()); // 인증 성공 후 삭제

        String tempPassword = generateTempPassword();
        member.setPassword(passwordEncoder.encode(tempPassword));
        memberRepository.save(member);

        emailService.sendTemporaryPasswordEmail(member.getEmail(), tempPassword);
    }

    private String generateTempPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String symbols = "!@#$%^&*()_+-=[]{}|;:,.<>?";

        String allChars = upper + lower + digits + symbols;
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();

        // 각 그룹에서 하나씩 무조건 포함되도록
        sb.append(upper.charAt(rnd.nextInt(upper.length())));
        sb.append(lower.charAt(rnd.nextInt(lower.length())));
        sb.append(digits.charAt(rnd.nextInt(digits.length())));
        sb.append(symbols.charAt(rnd.nextInt(symbols.length())));

        // 나머지 자리 채우기 (중복 3연속 방지)
        while (sb.length() < 10) {
            char nextChar = allChars.charAt(rnd.nextInt(allChars.length()));
            int len = sb.length();
            if (len >= 2 && sb.charAt(len - 1) == nextChar && sb.charAt(len - 2) == nextChar) {
                continue; // 3연속 문자 방지
            }
            sb.append(nextChar);
        }

        // 무작위 섞기 (앞쪽에 대문자/소문자 등 치우침 방지)
        char[] passwordChars = sb.toString().toCharArray();
        for (int i = passwordChars.length - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            char temp = passwordChars[i];
            passwordChars[i] = passwordChars[j];
            passwordChars[j] = temp;
        }

        return new String(passwordChars);
    }

}

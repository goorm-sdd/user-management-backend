package org.example.goormssd.usermanagementbackend.service.auth;

import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.domain.EmailVerification;
import org.example.goormssd.usermanagementbackend.domain.Member;
import org.example.goormssd.usermanagementbackend.exception.ErrorCode;
import org.example.goormssd.usermanagementbackend.exception.GlobalException;
import org.example.goormssd.usermanagementbackend.repository.EmailVerificationRepository;
import org.example.goormssd.usermanagementbackend.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final MemberRepository memberRepository;

    // 이메일 인증 코드를 생성하고 저장하는 메서드
    public String createVerificationEntry(Member member) {
        String code = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

        EmailVerification verification = EmailVerification.builder()
                .email(member.getEmail())
                .code(code)
                .expiresAt(expiresAt)
                .build();

        emailVerificationRepository.save(verification);

        return code;
    }

    // 이메일 인증 링크 클릭 시 인증 코드를 검증하는 메서드
    public void verifyEmailCode(String code) {
        EmailVerification verification = emailVerificationRepository.findByCode(code)
                .orElseThrow(() -> new GlobalException(ErrorCode.VERIFICATION_CODE_INVALID));

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new GlobalException(ErrorCode.VERIFICATION_CODE_EXPIRED);
        }

        Member member = memberRepository.findByEmail(verification.getEmail())
                .orElseThrow(() -> new GlobalException(ErrorCode.EMAIL_NOT_FOUND));

        if (member.isEmailVerified()) {
            throw new GlobalException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        member.setEmailVerified(true);
        memberRepository.save(member);
    }
}

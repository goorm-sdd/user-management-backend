package org.example.goormssd.usermanagementbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.domain.EmailVerification;
import org.example.goormssd.usermanagementbackend.repository.EmailVerificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;

    // 이메일 인증 코드 생성 및 저장
    public String generateAndSaveCode(String email) {
        String code = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);

        EmailVerification verification = EmailVerification.builder()
                .email(email)
                .code(code)
                .expiresAt(expiresAt)
                .verified(false)
                .build();

        emailVerificationRepository.save(verification);

        return code; // 나중에 이메일 발송에 사용
    }

    // 이메일 인증 코드 검증
    public void verifyCode(String code) {
        EmailVerification verification = emailVerificationRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 인증 코드입니다."));

        if (verification.isVerified()) {
            throw new IllegalStateException("이미 인증된 이메일입니다.");
        }

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("인증 코드가 만료되었습니다.");
        }

        verification.setVerified(true);
        emailVerificationRepository.save(verification);
    }

    // 이메일 인증 상태 확인
    public boolean isEmailVerified(String email) {
        Optional<EmailVerification> optional = emailVerificationRepository.findById(email);
        return optional.map(EmailVerification::isVerified).orElse(false);
    }
}

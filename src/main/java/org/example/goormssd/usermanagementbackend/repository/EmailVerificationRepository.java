package org.example.goormssd.usermanagementbackend.repository;

import org.example.goormssd.usermanagementbackend.domain.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, String> {
    // 이메일 인증 코드로 찾기 (링크 클릭 시 검증용)
    Optional<EmailVerification> findByCode(String code);
}

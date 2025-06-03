package org.example.goormssd.usermanagementbackend.repository;

import org.example.goormssd.usermanagementbackend.domain.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByCode(String code);
    Optional<EmailVerification> findByEmail(String email);
}

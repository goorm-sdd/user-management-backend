package org.example.goormssd.usermanagementbackend.repository;

import org.example.goormssd.usermanagementbackend.domain.PhoneVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, String> {
}

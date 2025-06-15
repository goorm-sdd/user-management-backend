package org.example.goormssd.usermanagementbackend.service.auth;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String code);

    void sendTemporaryPasswordEmail(String toEmail, String tempPassword); // ✅ 추가

}

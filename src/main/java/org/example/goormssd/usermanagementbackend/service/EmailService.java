package org.example.goormssd.usermanagementbackend.service;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String code);
}

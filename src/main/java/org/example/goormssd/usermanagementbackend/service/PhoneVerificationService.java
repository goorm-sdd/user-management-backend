package org.example.goormssd.usermanagementbackend.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.example.goormssd.usermanagementbackend.domain.PhoneVerification;
import org.example.goormssd.usermanagementbackend.repository.PhoneVerificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PhoneVerificationService {


    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.api.number}")
    private String fromPhoneNumber;

    private final PhoneVerificationRepository repository;

    private DefaultMessageService messageService;


    @PostConstruct
    public void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public void sendVerificationCode(String toPhoneNumber) {
        String code = generateRandomCode();

        Message message = new Message();
        message.setFrom(fromPhoneNumber);
        message.setTo(toPhoneNumber);
        message.setText("[회원가입 인증번호] " + code);

        try {
            messageService.sendOne(new SingleMessageSendingRequest(message));
        } catch (Exception e) {
            throw new RuntimeException("SMS 발송 실패", e);
        }

        // 인증 정보 DB에 저장 (갱신 or 신규 저장)
        repository.save(PhoneVerification.builder()
                .phoneNumber(toPhoneNumber)
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(3))
                .verified(false)
                .build());
    }

    private String generateRandomCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    public void verifyCode(String phoneNumber, String inputCode) {
        PhoneVerification verification = repository.findById(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("인증 요청 기록이 없습니다."));

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("인증번호가 만료되었습니다.");
        }

        if (!verification.getCode().equals(inputCode)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        verification.setVerified(true);
        repository.save(verification);
    }
}

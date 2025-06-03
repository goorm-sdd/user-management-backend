package org.example.goormssd.usermanagementbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.dto.request.PhoneVerifyRequestDto;
import org.example.goormssd.usermanagementbackend.service.PhoneVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/phone")
public class PhoneVerificationController {

    private final PhoneVerificationService phoneService;

    @PostMapping("/send")
    public ResponseEntity<?> sendCode(@RequestBody PhoneVerifyRequestDto requestDto) {
        phoneService.sendVerificationCode(requestDto.getPhoneNumber());
        return ResponseEntity.ok("인증번호가 발송되었습니다.");
    }
}

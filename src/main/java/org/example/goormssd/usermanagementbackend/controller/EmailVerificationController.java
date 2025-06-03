package org.example.goormssd.usermanagementbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.service.EmailVerificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    // 이메일 인증 링크 클릭 시 호출되는 API
    @GetMapping("/email/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("code") String code) {
        emailVerificationService.verifyEmailCode(code);

        // 인증 완료 후 프론트엔드의 인증 완료 페이지로 리디렉션
        URI redirectUri = URI.create("http://localhost:5173");

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(redirectUri)
                .build();
    }
}

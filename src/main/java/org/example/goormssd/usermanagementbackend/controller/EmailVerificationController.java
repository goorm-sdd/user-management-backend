package org.example.goormssd.usermanagementbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.service.EmailVerificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "User API", description = "일반 사용자 API")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    // 이메일 인증 링크 클릭 시 호출되는 API
    @Operation(
            summary = "이메일 인증 처리",
            description = "사용자가 이메일로 받은 인증 링크를 클릭하면 호출되는 API입니다. 이메일 인증 코드를 검증한 후 프론트엔드로 리디렉션됩니다.",
            tags = {"User API"}
    )
    @GetMapping("/email/verify")
    public ResponseEntity<String> verifyEmail(
            @Parameter(description = "이메일 인증 코드", example = "a1b2c3d4e5")
            @RequestParam("code") String code
    ) {
        emailVerificationService.verifyEmailCode(code);

        // 인증 완료 후 프론트엔드의 인증 완료 페이지로 리디렉션
        URI redirectUri = URI.create("http://localhost:5173");

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(redirectUri)
                .build();
    }
}

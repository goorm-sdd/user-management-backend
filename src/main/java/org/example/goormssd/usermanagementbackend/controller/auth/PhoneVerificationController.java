package org.example.goormssd.usermanagementbackend.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.dto.auth.requset.PhoneVerifyCodeRequestDto;
import org.example.goormssd.usermanagementbackend.dto.auth.requset.PhoneVerifyRequestDto;
import org.example.goormssd.usermanagementbackend.service.auth.PhoneVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/phone")
public class PhoneVerificationController {

    private final PhoneVerificationService phoneService;

    @Operation(
            summary = "휴대폰 인증번호 발송",
            description = "입력한 전화번호로 인증번호를 문자로 발송합니다."
    )
    @Tag(name = "인증 API", description = "회원가입, 로그인, 인증 관련 API입니다.")
    @PostMapping("/send")
    public ResponseEntity<?> sendCode(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "전화번호 입력 DTO", required = true)
            @Valid @RequestBody PhoneVerifyRequestDto requestDto
    ) {
        phoneService.sendVerificationCode(requestDto.getPhoneNumber());
        return ResponseEntity.ok("인증번호가 발송되었습니다.");
    }

    @Operation(
            summary = "휴대폰 인증번호 확인",
            description = "입력한 전화번호와 인증번호를 비교하여 인증을 완료합니다."
    )
    @Tag(name = "인증 API", description = "회원가입, 로그인, 인증 관련 API입니다.")
    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "휴대폰 번호 + 인증번호 입력 DTO", required = true)
            @Valid @RequestBody PhoneVerifyCodeRequestDto requestDto
    ) {
        phoneService.verifyCode(requestDto.getPhoneNumber(), requestDto.getCode());
        return ResponseEntity.ok("인증이 완료되었습니다.");
    }

}

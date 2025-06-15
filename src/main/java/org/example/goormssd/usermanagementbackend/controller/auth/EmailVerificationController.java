package org.example.goormssd.usermanagementbackend.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.dto.common.ApiResponseDto;
import org.example.goormssd.usermanagementbackend.service.auth.EmailVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"
)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    // 이메일 인증 링크 클릭 시 호출되는 API
    @Operation(
            summary = "이메일 인증 처리",
            description = "사용자가 이메일로 받은 인증 링크를 클릭하면 호출되는 API입니다. 이메일 인증 코드를 검증한 후 프론트엔드로 리디렉션됩니다."
    )
    @Tag(name = "인증 API", description = "회원가입, 로그인, 인증 관련 API입니다.")
    @GetMapping("/email/verify")
    public ResponseEntity<ApiResponseDto<String>> verifyEmail(
            @Parameter(description = "이메일 인증 코드", example = "a1b2c3d4e5")
            @RequestParam("code") String code
    ) {
        emailVerificationService.verifyEmailCode(code);

        // 인증 완료 후 프론트엔드의 인증 완료 페이지로 리디렉션
        String redirectUrl = "http://localhost:5173/email/verified";

        return ResponseEntity.ok(
                ApiResponseDto.of(200, "이메일 인증이 완료되었습니다. 아래 주소로 이동해주세요.", redirectUrl)
        );
    }
}

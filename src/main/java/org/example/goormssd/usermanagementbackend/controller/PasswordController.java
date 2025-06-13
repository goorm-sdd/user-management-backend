package org.example.goormssd.usermanagementbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.dto.request.PasswordVerifyRequestDto;
import org.example.goormssd.usermanagementbackend.dto.response.ReauthResponseDto;
import org.example.goormssd.usermanagementbackend.service.PasswordService;
import org.example.goormssd.usermanagementbackend.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"
)
@RestController
@RequestMapping("/api/users/password")
@RequiredArgsConstructor
@Tag(name = "User API", description = "일반 사용자 API")
public class PasswordController {
    private final PasswordService passwordService;
    private final JwtUtil jwtUtil;

    @Operation(
            summary = "비밀번호 재인증",
            description = "민감한 작업(이메일 변경, 회원 탈퇴 등)을 수행하기 전, 사용자의 비밀번호를 검증하고 재인증 토큰(reauthToken)을 발급합니다.",
            tags = {"User API"},
            security = @SecurityRequirement(name = "AccessToken")
    )
    @PostMapping("/verify")
    public ResponseEntity<ReauthResponseDto> verifyPassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "비밀번호 입력 DTO", required = true)
            @Valid @RequestBody PasswordVerifyRequestDto requestDto,
            @Parameter(hidden = true) Authentication auth
    ) {
        boolean ok = passwordService.verify(auth, requestDto.getPassword());
        if (!ok) {
            return ResponseEntity.status(401)
                    .body(new ReauthResponseDto(false, null, "비밀번호 불일치"));
        }
        String token = jwtUtil.generateReauthToken(
                (UserDetails) auth.getPrincipal());
        return ResponseEntity.ok(
                new ReauthResponseDto(true, token, "재인증 토큰 발급"));
    }
}

package org.example.goormssd.usermanagementbackend.controller.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.dto.auth.response.ReauthResponseDto;
import org.example.goormssd.usermanagementbackend.dto.common.ApiResponseDto;
import org.example.goormssd.usermanagementbackend.dto.member.request.PasswordVerifyRequestDto;
import org.example.goormssd.usermanagementbackend.security.JwtUtil;
import org.example.goormssd.usermanagementbackend.service.member.PasswordService;
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
public class PasswordController {
    private final PasswordService passwordService;
    private final JwtUtil jwtUtil;

    @Operation(
            summary = "비밀번호 재인증",
            description = "민감한 작업(이메일 변경, 회원 탈퇴 등)을 수행하기 전, 사용자의 비밀번호를 검증하고 재인증 토큰(reauthToken)을 발급합니다.",
            security = @SecurityRequirement(name = "AccessToken")
    )
    @Tag(name = "회원 API", description = "일반 회원 기능 관련 API입니다.")
    @PostMapping("/verify")
    public ResponseEntity<ApiResponseDto<ReauthResponseDto>> verifyPassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "비밀번호 입력 DTO", required = true)
            @Valid @RequestBody PasswordVerifyRequestDto requestDto,
            @Parameter(hidden = true) Authentication auth
    ) {
        boolean ok = passwordService.verify(auth, requestDto.getPassword());
        if (!ok) {
            return ResponseEntity.status(401)
                    .body(ApiResponseDto.of(401, "비밀번호가 일치하지 않습니다.", null));
        }

        String token = jwtUtil.generateReauthToken((UserDetails) auth.getPrincipal());

        ReauthResponseDto responseDto = new ReauthResponseDto(
                true,
                token
        );

        return ResponseEntity.ok(
                ApiResponseDto.of(200, "재인증 토큰이 발급되었습니다.", responseDto)
        );
    }
}

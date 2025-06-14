package org.example.goormssd.usermanagementbackend.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.dto.request.EmailCheckRequestDto;
import org.example.goormssd.usermanagementbackend.dto.request.SignupRequestDto;
import org.example.goormssd.usermanagementbackend.dto.response.ApiResponseDto;
import org.example.goormssd.usermanagementbackend.dto.response.MyProfileResponseDto;
import org.example.goormssd.usermanagementbackend.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"
)
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(
            summary = "회원가입",
            description = "이메일, 비밀번호, 사용자 이름 등의 정보를 입력받아 회원가입을 처리합니다. 이메일 인증도 함께 처리됩니다.",
            tags = { "Auth" }
    )
    @PostMapping("/auth/signup")
    public ResponseEntity<String> signup(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원가입 요청 데이터", required = true
            )
            @Valid @RequestBody SignupRequestDto requestDto
    ) {
        // 회원 저장 및 이메일 인증 처리까지 서비스에서 수행
        memberService.signup(requestDto);
        return ResponseEntity.ok("회원가입이 완료되었습니다. 이메일을 확인해주세요.");
    }

    @Operation(
            summary = "이메일 중복 확인",
            description = "입력한 이메일이 이미 가입된 이메일인지 확인합니다.",
            tags = { "Auth" }
    )
    @PostMapping("/auth/email")
    public ResponseEntity<Map<String, Object>> checkEmailDuplicate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "이메일 중복 확인 요청", required = true
            )
            @RequestBody @Valid EmailCheckRequestDto requestDto
    ) {
        boolean exists = memberService.isEmailDuplicate(requestDto.getEmail());

        Map<String, Object> response = new HashMap<>();
        if (exists) {
            response.put("status", 409);
            response.put("message", "중복된 이메일입니다.");
            return ResponseEntity.status(409).body(response);
        } else {
            response.put("status", 200);
            response.put("message", "사용가능한 이메일입니다.");
            return ResponseEntity.ok(response);
        }
    }

    @Operation(
            summary = "내 프로필 조회",
            description = "AccessToken을 기반으로 현재 로그인한 사용자의 프로필 정보를 조회합니다.",
            tags = { "User" },
            security = @SecurityRequirement(name = "AccessToken")
    )
    @GetMapping("/users/me")
    public ResponseEntity<ApiResponseDto<MyProfileResponseDto>> getMyProfile() {

        MyProfileResponseDto responseDto = memberService.getMyProfile();

        ApiResponseDto<MyProfileResponseDto> response = new ApiResponseDto<>(
                200,
                "User information retrieved successfully.",
                responseDto
        );

        return ResponseEntity.ok(response);
    }
}

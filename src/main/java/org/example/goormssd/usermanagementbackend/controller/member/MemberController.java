package org.example.goormssd.usermanagementbackend.controller.member;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.dto.admin.request.UpdateStatusRequestDto;
import org.example.goormssd.usermanagementbackend.dto.common.ApiResponseDto;
import org.example.goormssd.usermanagementbackend.dto.member.request.UpdatePasswordRequestDto;
import org.example.goormssd.usermanagementbackend.dto.member.request.UpdatePhoneRequestDto;
import org.example.goormssd.usermanagementbackend.dto.member.response.MyProfileResponseDto;
import org.example.goormssd.usermanagementbackend.security.UserDetailsImpl;
import org.example.goormssd.usermanagementbackend.service.member.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
            summary = "내 프로필 조회",
            description = "AccessToken을 기반으로 현재 로그인한 사용자의 프로필 정보를 조회합니다.",
            security = @SecurityRequirement(name = "AccessToken")
    )
    @Tag(name = "회원 API", description = "일반 회원 기능 관련 API입니다.")
    @GetMapping("/users/me")
    public ResponseEntity<ApiResponseDto<MyProfileResponseDto>> getMyProfile() {

        MyProfileResponseDto responseDto = memberService.getMyProfile();

        return ResponseEntity.ok(ApiResponseDto.of(200, "프로필 조회가 완료되었습니다.", responseDto));

    }

    @PatchMapping("/users/me/password")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "비밀번호 변경",
            description = "재인증 토큰(reauthToken)으로 인증된 사용자의 비밀번호를 변경합니다.",
            security = @SecurityRequirement(name = "ReauthToken")
    )
    @Tag(name = "회원 API", description = "일반 회원 기능 관련 API입니다.")
    public ResponseEntity<ApiResponseDto<Void>> updatePassword(
            @Valid @RequestBody UpdatePasswordRequestDto requestDto,
            @Parameter(hidden = true) Authentication authentication
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        memberService.updatePassword(userDetails.getMember(), requestDto);
        return ResponseEntity.ok(ApiResponseDto.of(200, "비밀번호가 성공적으로 변경되었습니다.", null));
    }


    @PatchMapping("/users/me/phone")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "전화번호 변경",
            description = "인증된 전화번호로 사용자의 정보를 변경합니다.",
            security = @SecurityRequirement(name = "ReauthToken")
    )
    @Tag(name = "회원 API", description = "일반 회원 기능 관련 API입니다.")
    public ResponseEntity<ApiResponseDto<Void>> updatePhoneNumber(
            @Valid @RequestBody UpdatePhoneRequestDto requestDto,
            @Parameter(hidden = true) Authentication authentication
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        memberService.updatePhoneNumber(userDetails.getMember(), requestDto.getPhoneNumber());
        return ResponseEntity.ok(ApiResponseDto.of(200, "전화번호가 성공적으로 변경되었습니다.", null));
    }

    @PatchMapping("/users/me/status")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "회원 상태 변경 (탈퇴 또는 복구)",
            description = "reauthToken으로 인증된 사용자의 상태를 변경합니다.",
            security = @SecurityRequirement(name = "ReauthToken")
    )
    @Tag(name = "회원 API", description = "일반 회원 기능 관련 API입니다.")
    public ResponseEntity<ApiResponseDto<Void>> updateMyStatus(
            @Valid @RequestBody UpdateStatusRequestDto requestDto,
            @Parameter(hidden = true) Authentication authentication
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        memberService.updateStatus(userDetails.getMember(), requestDto.getStatus());
        return ResponseEntity.ok(ApiResponseDto.of(200, "회원 상태가 성공적으로 변경되었습니다.", null));
    }
}

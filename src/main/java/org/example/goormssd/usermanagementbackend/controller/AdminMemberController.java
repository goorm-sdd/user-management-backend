package org.example.goormssd.usermanagementbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.dto.response.*;
import org.example.goormssd.usermanagementbackend.service.AdminMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"
)
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin API", description = "관리자 권한이 필요한 API")
@SecurityRequirement(name = "JWT")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @Operation(
            summary = "대시보드 조회",
            description = "전체 회원, 탈퇴 회원 수를 포함한 관리자용 대시보드 데이터를 조회합니다.",
            tags = {"Admin API"},
            security = @SecurityRequirement(name = "AccessToken")
    )
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<DashboardResponseDto>> getDashboard(
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(name = "pageNum",   defaultValue = "1")  int pageNum,

            @Parameter(description = "한 페이지당 회원 수", example = "10")
            @RequestParam(name = "pageLimit", defaultValue = "10") int pageLimit,

            @Parameter(description = "이메일 인증 여부 (true/false)", example = "true")
            @RequestParam(name = "emailVerified", required = false) Boolean emailVerified,

            @Parameter(description = "회원 상태 (active/deleted)", example = "active")
            @RequestParam(name = "status", required = false) String status
    ) {
        DashboardResponseDto responseDto = adminMemberService.getDashboard(
                pageNum, pageLimit, emailVerified, status);

        ApiResponseDto<DashboardResponseDto> response =
                ApiResponseDto.of(200, "대시 보드 조회 성공", responseDto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "전체 회원 조회",
            description = "가입된 전체 회원 목록을 페이징, 정렬 기준에 따라 조회합니다.",
            tags = {"Admin API"},
            security = @SecurityRequirement(name = "AccessToken")
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<MemberListResponseDto>> getAllMembers(
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,

            @Parameter(description = "한 페이지당 회원 수", example = "10")
            @RequestParam(name = "pageLimit", defaultValue = "10") int pageLimit,

            @Parameter(description = "정렬 기준 컬럼명", example = "createdAt")
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,

            @Parameter(description = "정렬 방향 (asc 또는 desc)", example = "desc")
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir
    ) {
        MemberListResponseDto dto = adminMemberService.getAllMembers(pageNum, pageLimit, sortBy, sortDir);
        return ResponseEntity.ok(
                ApiResponseDto.of(200, "전체 회원 조회 성공", dto)
        );
    }

    @Operation(
            summary = "탈퇴 회원 조회",
            description = "삭제(deleted) 상태인 회원 목록을 조회합니다.",
            tags = {"Admin API"},
            security = @SecurityRequirement(name = "AccessToken")
    )
    @GetMapping("/deleted")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<MemberListResponseDto>> getDeletedMembers(
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,

            @Parameter(description = "한 페이지당 회원 수", example = "10")
            @RequestParam(name = "pageLimit", defaultValue = "10") int pageLimit,

            @Parameter(description = "정렬 기준 컬럼명", example = "createdAt")
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,

            @Parameter(description = "정렬 방향 (asc 또는 desc)", example = "desc")
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir
    ) {
        MemberListResponseDto dto = adminMemberService.getDeletedMembers(pageNum, pageLimit, sortBy, sortDir);
        return ResponseEntity.ok(
                ApiResponseDto.of(200, "탈퇴 회원 조회 성공", dto)
        );
    }

    @Operation(
            summary = "이메일 미인증 회원 조회",
            description = "이메일 인증이 완료되지 않은 회원 목록을 조회합니다.",
            tags = {"Admin API"},
            security = @SecurityRequirement(name = "AccessToken")
    )
    @GetMapping("/unverified")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<MemberListResponseDto>> getUnverifiedEmailMembers(
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,

            @Parameter(description = "한 페이지당 회원 수", example = "10")
            @RequestParam(name = "pageLimit", defaultValue = "10") int pageLimit,

            @Parameter(description = "정렬 기준 컬럼명", example = "createdAt")
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,

            @Parameter(description = "정렬 방향 (asc 또는 desc)", example = "desc")
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir
    ) {
        MemberListResponseDto dto = adminMemberService.getUnverifiedEmailMembers(pageNum, pageLimit, sortBy, sortDir);
        return ResponseEntity.ok(
                ApiResponseDto.of(200, "이메일 미인증 회원 조회 성공", dto)
        );
    }

    @Operation(
            summary = "회원 상세 정보 조회",
            description = "회원 ID를 기반으로 해당 사용자의 상세 정보를 조회합니다.",
            tags = {"Admin API"},
            security = @SecurityRequirement(name = "AccessToken")
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<MemberDetailResponseDto>> getMemberDetail(
            @Parameter(description = "회원 ID", example = "1")
            @PathVariable Long id
    ) {
        MemberDetailResponseDto dto = adminMemberService.getMemberDetailById(id);
        return ResponseEntity.ok(ApiResponseDto.of(200, "User information retrieved successfully.", dto));
    }

    @Operation(
            summary = "회원 검색",
            description = "회원의 이메일 또는 사용자 이름으로 검색합니다. 두 항목을 동시에 사용할 수 없습니다.",
            tags = {"Admin API"},
            security = @SecurityRequirement(name = "AccessToken")
    )
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<MemberListResponseDto>> searchMembers(
            @Parameter(description = "이메일로 검색 (username과 동시 사용 불가)", example = "user@example.com")
            @RequestParam(required = false) String email,

            @Parameter(description = "사용자 이름으로 검색 (email과 동시 사용 불가)", example = "홍길동")
            @RequestParam(required = false) String username,

            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,

            @Parameter(description = "한 페이지당 회원 수", example = "10")
            @RequestParam(name = "pageLimit", defaultValue = "10") int pageLimit,

            @Parameter(description = "정렬 기준 컬럼명", example = "createdAt")
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,

            @Parameter(description = "정렬 방향 (asc 또는 desc)", example = "desc")
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,

            @Parameter(description = "이메일 인증 여부 (true/false)", example = "true")
            @RequestParam(name = "emailVerified", required = false) Boolean emailVerified,

            @Parameter(description = "회원 상태 (active/deleted)", example = "active")
            @RequestParam(name = "status", required = false) String status
    ) {
        // email과 username 동시 사용 제한
        if (email != null && username != null) {
            return ResponseEntity.badRequest().body(
                    ApiResponseDto.of(400, "email과 username은 동시에 검색할 수 없습니다.", null)
            );
        }

        MemberListResponseDto dto = adminMemberService.searchMembers(
                email, username, pageNum, pageLimit, sortBy, sortDir, emailVerified, status
        );
        return ResponseEntity.ok(
                ApiResponseDto.of(200, "회원 검색 성공", dto)
        );
    }
}

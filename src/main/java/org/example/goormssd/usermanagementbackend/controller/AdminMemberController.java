package org.example.goormssd.usermanagementbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.dto.response.*;
import org.example.goormssd.usermanagementbackend.service.AdminMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<DashboardResponseDto>> getDashboard(
            @RequestParam(name = "pageNum",   defaultValue = "1")  int pageNum,
            @RequestParam(name = "pageLimit", defaultValue = "10") int pageLimit
    ) {
        DashboardResponseDto responseDto = adminMemberService.getDashboard(pageNum, pageLimit);
        ApiResponseDto<DashboardResponseDto> response =
                ApiResponseDto.of(200, "대시 보드 조회 성공", responseDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<MemberListResponseDto>> getAllMembers(
            @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(name = "pageLimit", defaultValue = "10") int pageLimit,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir
    ) {
        MemberListResponseDto dto = adminMemberService.getAllMembers(pageNum, pageLimit, sortBy, sortDir);
        return ResponseEntity.ok(
                ApiResponseDto.of(200, "전체 회원 조회 성공", dto)
        );
    }

    @GetMapping("/deleted")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<MemberListResponseDto>> getDeletedMembers(
            @RequestParam(name = "pageNum",   defaultValue = "1")         int pageNum,
            @RequestParam(name = "pageLimit", defaultValue = "10")        int pageLimit,
            @RequestParam(name = "sortBy",    defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDir",   defaultValue = "desc")      String sortDir
    ) {
        MemberListResponseDto dto = adminMemberService.getDeletedMembers(pageNum, pageLimit, sortBy, sortDir);
        return ResponseEntity.ok(
                ApiResponseDto.of(200, "탈퇴 회원 조회 성공", dto)
        );
    }

    @GetMapping("/unverified")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<MemberListResponseDto>> getUnverifiedEmailMembers(
            @RequestParam(name = "pageNum",   defaultValue = "1")         int pageNum,
            @RequestParam(name = "pageLimit", defaultValue = "10")        int pageLimit,
            @RequestParam(name = "sortBy",    defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDir",   defaultValue = "desc")      String sortDir
    ) {
        MemberListResponseDto dto = adminMemberService.getUnverifiedEmailMembers(pageNum, pageLimit, sortBy, sortDir);
        return ResponseEntity.ok(
                ApiResponseDto.of(200, "이메일 미인증 회원 조회 성공", dto)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<MemberDetailResponseDto>> getMemberDetail(
            @PathVariable Long id
    ) {
        MemberDetailResponseDto dto = adminMemberService.getMemberDetailById(id);
        return ResponseEntity.ok(ApiResponseDto.of(200, "User information retrieved successfully.", dto));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<MemberListResponseDto>> searchMembers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String username,
            @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(name = "pageLimit", defaultValue = "10") int pageLimit,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir
    ) {
        // email과 username 동시 사용 제한
        if (email != null && username != null) {
            return ResponseEntity.badRequest().body(
                    ApiResponseDto.of(400, "email과 username은 동시에 검색할 수 없습니다.", null)
            );
        }

        MemberListResponseDto dto = adminMemberService.searchMembers(email, username, pageNum, pageLimit, sortBy, sortDir);
        return ResponseEntity.ok(
                ApiResponseDto.of(200, "회원 검색 성공", dto)
        );
    }
}

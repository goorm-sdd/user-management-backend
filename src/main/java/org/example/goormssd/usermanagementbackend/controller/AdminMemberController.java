package org.example.goormssd.usermanagementbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.dto.response.ApiResponseDto;
import org.example.goormssd.usermanagementbackend.dto.response.DashboardResponseDto;
import org.example.goormssd.usermanagementbackend.dto.response.MemberListResponseDto;
import org.example.goormssd.usermanagementbackend.service.AdminMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}

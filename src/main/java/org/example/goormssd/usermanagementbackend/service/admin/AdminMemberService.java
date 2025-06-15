package org.example.goormssd.usermanagementbackend.service.admin;

import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.domain.Member;
import org.example.goormssd.usermanagementbackend.dto.admin.response.DashboardResponseDto;
import org.example.goormssd.usermanagementbackend.dto.admin.response.MemberDetailResponseDto;
import org.example.goormssd.usermanagementbackend.dto.admin.response.MemberListResponseDto;
import org.example.goormssd.usermanagementbackend.dto.member.request.MemberSearchConditionDto;
import org.example.goormssd.usermanagementbackend.dto.member.response.MemberResponseDto;
import org.example.goormssd.usermanagementbackend.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminMemberService {

    private final MemberRepository memberRepository;

    public DashboardResponseDto getDashboard(
            int pageNum, int pageLimit, Boolean emailVerified, Member.Status status
    ) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageLimit, Sort.by("createdAt").descending());

        MemberSearchConditionDto condition = new MemberSearchConditionDto();
        condition.setEmailVerified(emailVerified);
        condition.setStatus(status);

        Page<Member> page = memberRepository.searchMembers(condition, pageable);

        List<MemberResponseDto> users = page.getContent().stream()
                .map(MemberResponseDto::from)
                .collect(Collectors.toList());

        long sumUser = memberRepository.count();
        long deletedUser = memberRepository.countByStatus(Member.Status.DELETED);

        return new DashboardResponseDto(
                sumUser,
                deletedUser,
                users,
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    public MemberDetailResponseDto getMemberDetailById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 회원이 존재하지 않습니다. id=" + id));
        return new MemberDetailResponseDto(
                member.getId(),
                member.getUsername(),
                member.getEmail(),
                member.getPhoneNumber(),
                member.getPassword()
        );
    }

    public MemberListResponseDto searchMembers(
            String email, String username,
            int pageNum, int pageLimit,
            String sortBy, String sortDir,
            Boolean emailVerified, Member.Status status
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(pageNum - 1, pageLimit, Sort.by(direction, sortBy));

        MemberSearchConditionDto condition = new MemberSearchConditionDto();
        condition.setEmail(email);
        condition.setUsername(username);
        condition.setEmailVerified(emailVerified);
        condition.setStatus(status);

        Page<Member> page = memberRepository.searchMembers(condition, pageable);

        List<MemberResponseDto> users = page.getContent().stream()
                .map(MemberResponseDto::from)
                .collect(Collectors.toList());

        return new MemberListResponseDto(
                users,
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }


    public void updateStatus(Long id, String status) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 회원이 존재하지 않습니다."));

        if ("active".equalsIgnoreCase(status)) {
            member.setStatus(Member.Status.ACTIVE);
        } else if ("deleted".equalsIgnoreCase(status)) {
            member.setStatus(Member.Status.DELETED);
        } else {
            throw new IllegalArgumentException("Invalid status value. Must be 'active' or 'deleted'.");
        }

        memberRepository.save(member);
    }


    // 현재 클라이언트에서 사용되고 있지 않음
    public MemberListResponseDto getAllMembers(int pageNum, int pageLimit, String sortBy, String sortDir) {
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(pageNum - 1, pageLimit, Sort.by(direction, sortBy));
        Page<Member> page = memberRepository.findAll(pageable);

        List<MemberResponseDto> users = page.getContent().stream()
                .map(member -> new MemberResponseDto(
                        member.getId(),
                        member.getUsername(),
                        member.getEmail(),
                        member.getPhoneNumber(),
                        member.getRole().name(),
                        member.getStatus().name().toLowerCase(),
                        member.isEmailVerified(),
                        member.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new MemberListResponseDto(
                users,
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    public MemberListResponseDto getDeletedMembers(int pageNum, int pageLimit, String sortBy, String sortDir) {
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(pageNum - 1, pageLimit, Sort.by(direction, sortBy));

        Page<Member> page = memberRepository.findAllByStatus(Member.Status.DELETED, pageable);

        List<MemberResponseDto> users = page.getContent().stream()
                .map(member -> new MemberResponseDto(
                        member.getId(),
                        member.getUsername(),
                        member.getEmail(),
                        member.getPhoneNumber(),
                        member.getRole().name(),
                        member.getStatus().name().toLowerCase(),
                        member.isEmailVerified(),
                        member.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new MemberListResponseDto(
                users,
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    public MemberListResponseDto getUnverifiedEmailMembers(int pageNum, int pageLimit, String sortBy, String sortDir) {
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(pageNum - 1, pageLimit, Sort.by(direction, sortBy));

        Page<Member> page = memberRepository.findAllByEmailVerifiedFalse(pageable);

        List<MemberResponseDto> users = page.getContent().stream()
                .map(member -> new MemberResponseDto(
                        member.getId(),
                        member.getUsername(),
                        member.getEmail(),
                        member.getPhoneNumber(),
                        member.getRole().name(),
                        member.getStatus().name().toLowerCase(),
                        member.isEmailVerified(),
                        member.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new MemberListResponseDto(
                users,
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
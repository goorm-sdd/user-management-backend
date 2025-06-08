package org.example.goormssd.usermanagementbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.domain.Member;
import org.example.goormssd.usermanagementbackend.dto.response.DashboardResponseDto;
import org.example.goormssd.usermanagementbackend.dto.response.MemberListResponseDto;
import org.example.goormssd.usermanagementbackend.dto.response.MemberResponseDto;
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

    public DashboardResponseDto getDashboard(int pageNum, int pageLimit) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageLimit, Sort.by("createdAt").descending());

        Page<Member> page = memberRepository.findAll(pageable);

        List<MemberResponseDto> users = memberRepository.findAll(pageable)
                .stream()
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

        long sumUser     = memberRepository.count();
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
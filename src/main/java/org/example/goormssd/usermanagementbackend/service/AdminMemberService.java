package org.example.goormssd.usermanagementbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.domain.Member;
import org.example.goormssd.usermanagementbackend.dto.response.DashboardResponseDto;
import org.example.goormssd.usermanagementbackend.dto.response.MemberResponseDto;
import org.example.goormssd.usermanagementbackend.repository.MemberRepository;
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

        return new DashboardResponseDto(sumUser, deletedUser, users);
    }
}
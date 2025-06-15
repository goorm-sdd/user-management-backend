package org.example.goormssd.usermanagementbackend.dto.admin.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.goormssd.usermanagementbackend.dto.member.response.MemberResponseDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDto {
    private long sumUser;
    private long deletedUser;
    private List<MemberResponseDto> users;

    private int currentPage;
    private int pageLimit;
    private int totalPages;
    private long totalElements;
}

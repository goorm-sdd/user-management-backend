package org.example.goormssd.usermanagementbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberListResponseDto {
    private List<MemberResponseDto> users;
    private int currentPage;
    private int pageLimit;
    private int totalPages;
    private long totalElements;
}

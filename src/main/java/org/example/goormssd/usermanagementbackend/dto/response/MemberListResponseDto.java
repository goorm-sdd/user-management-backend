package org.example.goormssd.usermanagementbackend.dto.response;

import lombok.*;

import java.util.List;

@Builder
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

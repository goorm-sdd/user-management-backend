package org.example.goormssd.usermanagementbackend.dto.member.request;

import lombok.*;
import org.example.goormssd.usermanagementbackend.domain.Member;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSearchConditionDto {

    private String email;
    private String username;
    private Boolean emailVerified;
    private Member.Status status;

}

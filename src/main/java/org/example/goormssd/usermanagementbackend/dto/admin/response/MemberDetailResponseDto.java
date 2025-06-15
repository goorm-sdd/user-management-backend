package org.example.goormssd.usermanagementbackend.dto.admin.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDetailResponseDto {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
}

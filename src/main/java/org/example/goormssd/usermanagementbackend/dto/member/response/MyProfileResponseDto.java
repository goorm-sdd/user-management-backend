package org.example.goormssd.usermanagementbackend.dto.member.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyProfileResponseDto {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String Password;
}

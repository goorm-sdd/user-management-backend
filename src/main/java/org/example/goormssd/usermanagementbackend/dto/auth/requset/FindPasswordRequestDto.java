package org.example.goormssd.usermanagementbackend.dto.auth.requset;

import lombok.Getter;

@Getter
public class FindPasswordRequestDto {
    private String username;
    private String email;
}
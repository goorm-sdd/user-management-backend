package org.example.goormssd.usermanagementbackend.dto.request;

import lombok.Getter;

@Getter
public class FindPasswordRequestDto {
    private String username;
    private String email;
}
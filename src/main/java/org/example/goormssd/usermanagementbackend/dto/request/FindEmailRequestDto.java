package org.example.goormssd.usermanagementbackend.dto.request;

import lombok.Getter;

@Getter
public class FindEmailRequestDto {
    private String username;
    private String phoneNumber;
    private String code; // 인증번호 입력
}
package org.example.goormssd.usermanagementbackend.dto.auth.requset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class FindEmailRequestDto {

    private String username;

    @NotBlank(message = "전화번호는 필수 입력입니다.")
    @Pattern(regexp = "^010\\d{8}$", message = "올바른 형식의 전화번호여야 합니다.")
    private String phoneNumber;

    private String code; // 인증번호 입력
}
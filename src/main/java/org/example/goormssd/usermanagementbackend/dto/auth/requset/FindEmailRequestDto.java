package org.example.goormssd.usermanagementbackend.dto.auth.requset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class FindEmailRequestDto {

    @NotBlank(message = "이름은 필수 입력입니다.")
    private String username;

    @NotBlank(message = "전화번호는 필수 입력입니다.")
    @Pattern(regexp = "^010\\d{8}$", message = "올바른 형식의 전화번호여야 합니다.")
    private String phoneNumber;

    @NotBlank(message = "인증번호는 필수 입력입니다.")
    @Pattern(regexp = "^\\d{6}$", message = "인증번호는 숫자 6자리여야 합니다.")
    private String code; // 인증번호 입력
}
package org.example.goormssd.usermanagementbackend.dto.auth.requset;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailCheckRequestDto {

    @NotBlank(message = "이메일은 필수 입력입니다.")
    @Email(message = "유효한 이메일 형식을 입력해주세요.")
    private String email;
}

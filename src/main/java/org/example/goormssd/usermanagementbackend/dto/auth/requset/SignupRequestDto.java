package org.example.goormssd.usermanagementbackend.dto.auth.requset;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.example.goormssd.usermanagementbackend.validation.NoTripleRepeat;

@Getter
@Setter
public class SignupRequestDto {

    @NotBlank(message = "사용자 이름은 필수 입력입니다.")
    private String username;

    @NotBlank(message = "이메일은 필수 입력입니다.")
    @Email(message = "유효한 이메일 형식을 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 대소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    @NoTripleRepeat
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수 입력입니다.")
    private String passwordCheck;

    @NotBlank(message = "전화번호는 필수 입력입니다.")
    @Pattern(regexp = "^010\\d{8}$", message = "올바른 형식의 전화번호여야 합니다.")
    private String phoneNumber;

    @NotBlank(message = "인증 코드는 필수입니다.")
    @Pattern(regexp = "^\\d{6}$", message = "인증 코드는 6자리 숫자여야 합니다.")
    private String code;

    @AssertTrue(message = "비밀번호와 비밀번호 확인이 일치하지 않습니다.")
    public boolean isPasswordMatching() {
        return password != null && password.equals(passwordCheck);
    }
}

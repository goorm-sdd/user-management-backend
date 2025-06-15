package org.example.goormssd.usermanagementbackend.dto.member.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.example.goormssd.usermanagementbackend.validation.NoTripleRepeat;

@Getter
@Setter
public class UpdatePasswordRequestDto {

    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 대소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    @NoTripleRepeat
    private String newPassword;

    @NotBlank(message = "비밀번호 확인은 필수 입력입니다.")
    private String newPasswordCheck;

}
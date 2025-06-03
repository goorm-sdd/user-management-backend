package org.example.goormssd.usermanagementbackend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.example.goormssd.usermanagementbackend.util.NoTripleRepeat;

@Getter
@Setter
public class SignupRequestDto {

    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 대소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    @NoTripleRepeat
    private String password;

    @NotBlank
    private String passwordCheck;

    @NotBlank
    private String phoneNumber;


}

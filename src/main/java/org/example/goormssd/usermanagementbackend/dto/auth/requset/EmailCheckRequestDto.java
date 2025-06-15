package org.example.goormssd.usermanagementbackend.dto.auth.requset;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailCheckRequestDto {

    @NotBlank
    @Email
    private String email;
}

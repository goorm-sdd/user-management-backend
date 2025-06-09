package org.example.goormssd.usermanagementbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordVerifyResponseDto {

    private boolean valid;
    private String message; // valid=false일 때만 사용

}

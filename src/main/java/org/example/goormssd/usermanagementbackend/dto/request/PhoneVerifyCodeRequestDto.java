package org.example.goormssd.usermanagementbackend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhoneVerifyCodeRequestDto {
    private String phoneNumber;
    private String code;
}

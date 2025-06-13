package org.example.goormssd.usermanagementbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto<T> {
    private int status;
    private String message;
    private T data;

    public static <T> ApiResponseDto<T> of(int status, String message, T data) {
        return new ApiResponseDto<>(status, message, data);
    }
}
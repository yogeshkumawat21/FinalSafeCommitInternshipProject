package com.App.Yogesh.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto<T> {

    private String message;
    private int status;
    private T data; // Generic data field to hold the response data
}

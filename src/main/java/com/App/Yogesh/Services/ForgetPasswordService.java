package com.App.Yogesh.Services;

import com.App.Yogesh.ResponseDto.ApiResponseDto;
import org.springframework.http.ResponseEntity;

public interface ForgetPasswordService {
    ResponseEntity<ApiResponseDto<?>> verifyEmail(String email);
    ResponseEntity<ApiResponseDto<?>> verifyOtp(Integer otp
            , String email);
    ResponseEntity<ApiResponseDto<?>>changePassword(String newPassword , String email);
}

package com.App.Yogesh.Services;

import com.App.Yogesh.RequestDto.CreateUserRequestDto;
import com.App.Yogesh.RequestDto.LoginRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<ApiResponseDto<?>> getCurrentUserData();
    ResponseEntity<ApiResponseDto<?>> signUpUser(CreateUserRequestDto createUserRequestDto) throws Exception;
    ResponseEntity<ApiResponseDto<?>> LoginUser(LoginRequestDto loginRequestDto) throws Exception;
    ResponseEntity<ApiResponseDto<?>> LoginByOauth(org.springframework.security.core.Authentication authentication) throws Exception;
}

package com.App.Yogesh.Services;

import com.App.Yogesh.Models.User;
import com.App.Yogesh.RequestDto.UpdateUserRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import org.springframework.http.ResponseEntity;

public interface UserService {

    User registeruser(User user);

    ResponseEntity<ApiResponseDto<?>> getAllUser();

    ResponseEntity<ApiResponseDto<?>> getUserById(Integer id);

    ResponseEntity<ApiResponseDto<?>> updateUser(UpdateUserRequestDto updateUserRequestDto);

    ResponseEntity<ApiResponseDto<?>> followUser(Integer userId);

    ResponseEntity<ApiResponseDto<?>> searchUserByArguments(String query);

    ResponseEntity<ApiResponseDto<?>> currentUserProfile();

    void saveToken(Integer userId, String token) throws Exception; // Updated method signature
}


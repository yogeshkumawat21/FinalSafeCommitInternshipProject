package com.App.Yogesh.Services;

import com.App.Yogesh.RequestDto.BlockUserRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import org.springframework.http.ResponseEntity;

public interface AdminService {

    ResponseEntity<ApiResponseDto<?>> getAllUser();
    ResponseEntity<ApiResponseDto<?>> blockUser(BlockUserRequestDto blockUserRequestDto);
    ResponseEntity<ApiResponseDto<?>> unblockUser(BlockUserRequestDto blockUserRequestDto);
    ResponseEntity<ApiResponseDto<?>>  deletePost(Integer postId);
    ResponseEntity<ApiResponseDto<?>>  deleteComment(Integer commentId);
    ResponseEntity<ApiResponseDto<?>>  getUserProfile(Integer userId);
    ResponseEntity<ApiResponseDto<?>>  getAdminStatics();
    ResponseEntity<ApiResponseDto<?>>  addAdmin(Integer userId);
    ResponseEntity<ApiResponseDto<?>>  removeAdmin(Integer userId);
  }

package com.App.Yogesh.Services;

import com.App.Yogesh.RequestDto.CommentRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import org.springframework.http.ResponseEntity;

public interface CommentService {

    ResponseEntity<ApiResponseDto<?>> createComment(CommentRequestDto commentRequestDto );
    ResponseEntity<ApiResponseDto<?>> likeComment(Integer commentId);

}

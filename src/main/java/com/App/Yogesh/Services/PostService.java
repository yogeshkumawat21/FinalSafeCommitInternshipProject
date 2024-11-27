package com.App.Yogesh.Services;

import com.App.Yogesh.Models.Post;
import com.App.Yogesh.RequestDto.PostRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import org.springframework.http.ResponseEntity;

/**
 * Interface defining operations for managing posts.
 */
public interface PostService {

    ResponseEntity<ApiResponseDto<?>> createNewPost(PostRequestDto post);
    ResponseEntity<ApiResponseDto<?>> deletePostbyId(Integer postId);
    ResponseEntity<ApiResponseDto<?>> findPostById(Integer postId);
    ResponseEntity<ApiResponseDto<?>> findAllPostByUserId(Integer userId);
     ResponseEntity<ApiResponseDto<?>> findAllPost();
    ResponseEntity<ApiResponseDto<?>> likePost(Integer postId);

}

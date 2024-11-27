package com.App.Yogesh.Services;

import com.App.Yogesh.RequestDto.StoryRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import org.springframework.http.ResponseEntity;

public interface StoryService {
    ResponseEntity<ApiResponseDto<?>> createStroy(StoryRequestDto storyRequestDto);
    ResponseEntity<ApiResponseDto<?>> findStoryByUserId(Integer userId) throws Exception;

}

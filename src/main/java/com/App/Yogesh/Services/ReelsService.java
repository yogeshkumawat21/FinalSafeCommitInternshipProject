package com.App.Yogesh.Services;

import com.App.Yogesh.RequestDto.ReelsRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import org.springframework.http.ResponseEntity;

public interface ReelsService {


    ResponseEntity<ApiResponseDto<?>> createReels(ReelsRequestDto reels);
    ResponseEntity<ApiResponseDto<?>> findAllReels();
    ResponseEntity<ApiResponseDto<?>> findReelByUserId(Integer userId);


}

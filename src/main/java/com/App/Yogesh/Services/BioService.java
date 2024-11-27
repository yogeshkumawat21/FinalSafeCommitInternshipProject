package com.App.Yogesh.Services;

import com.App.Yogesh.RequestDto.BioRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import org.springframework.http.ResponseEntity;

public interface BioService {


    // Create or update bio
    public ResponseEntity<ApiResponseDto<?>> createOrUpdateBio(int userId,
                                                            BioRequestDto bioRequestDto) ;

    // Fetch bio for a user
    public BioRequestDto getBio(int userId) ;


}
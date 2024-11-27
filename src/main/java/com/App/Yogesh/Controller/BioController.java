package com.App.Yogesh.Controller;

import com.App.Yogesh.RequestDto.BioRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import com.App.Yogesh.Services.BioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class BioController {

    private final BioService bioService;

    @PostMapping("/setBio/{userId}")
    public ResponseEntity<ApiResponseDto<?>> setBio(@PathVariable Integer userId, @RequestBody BioRequestDto bioRequestDto) {
        log.info("Setting bio for user ID: {} with bio details: {}", userId, bioRequestDto);
        return bioService.createOrUpdateBio(userId, bioRequestDto);
    }
}

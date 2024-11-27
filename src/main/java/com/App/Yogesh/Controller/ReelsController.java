package com.App.Yogesh.Controller;

import com.App.Yogesh.RequestDto.ReelsRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import com.App.Yogesh.Services.ReelsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reels APIs")
@RestController
@RequestMapping("/api/reels")
@RequiredArgsConstructor
@Slf4j
public class ReelsController {

    private final ReelsService reelsService;

    @Operation(summary = "Create Reel")
    @PostMapping("create")
    public ResponseEntity<ApiResponseDto<?>> createReels(@RequestBody ReelsRequestDto reelsRequestDto) {
        log.info("Creating reel with details: {}", reelsRequestDto);
        return reelsService.createReels(reelsRequestDto);
    }

    @Operation(summary = "Find All Reels")
    @GetMapping
    public ResponseEntity<ApiResponseDto<?>> findAllReels() {
        log.info("Fetching all reels");
        return reelsService.findAllReels();
    }

    @Operation(summary = "Find Reels by User ID")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<?>> findAllReels(@PathVariable Integer userId) throws Exception {
        log.info("Fetching reels for user ID: {}", userId);
        return reelsService.findReelByUserId(userId);
    }
}

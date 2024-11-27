package com.App.Yogesh.Controller;

import com.App.Yogesh.RequestDto.StoryRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import com.App.Yogesh.Services.StoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Story APIs")
@RestController
@RequestMapping("/api/stories")
@RequiredArgsConstructor
@Slf4j
public class StoryController {

    private final StoryService storyService;

    @Operation(summary = "Create Story")
    @PostMapping("/createStory")
    public ResponseEntity<ApiResponseDto<?>> createStory(@RequestBody StoryRequestDto storyRequestDto) throws Exception {
        log.info("Creating story with details: {}", storyRequestDto);
        return storyService.createStroy(storyRequestDto);
    }
    @Operation(summary = "Find Story by User ID")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponseDto<?>> findUserStory(@PathVariable Integer userId) throws Exception {
        log.info("Fetching story for user ID: {}", userId);
        return storyService.findStoryByUserId(userId);
    }
}

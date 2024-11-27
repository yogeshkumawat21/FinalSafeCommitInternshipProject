package com.App.Yogesh.Controller;
import com.App.Yogesh.RequestDto.CommentRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import com.App.Yogesh.Services.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment APIs")
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    // API for Creating Comment
    @Operation(summary = "Create Comment")
    @PostMapping("/createComment")
    public ResponseEntity<ApiResponseDto<?>> createComment(@RequestBody CommentRequestDto commentRequestDto) throws Exception {
        log.info("Creating comment with details: {}", commentRequestDto);
        return commentService.createComment(commentRequestDto);
    }

    // API for Liking a Comment
    @Operation(summary = "Like Comment")
    @PostMapping("/like/{commentId}")
    public ResponseEntity<ApiResponseDto<?>> likeComment(@PathVariable("commentId") Integer commentId) throws Exception {
        log.info("Liking comment with ID: {}", commentId);
        return commentService.likeComment(commentId);
    }
}

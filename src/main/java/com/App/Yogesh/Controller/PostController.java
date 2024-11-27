package com.App.Yogesh.Controller;

import com.App.Yogesh.RequestDto.PostRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import com.App.Yogesh.Services.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Post APIs")
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    @Operation(summary = "Create Post")
    @PostMapping("/createPost")
    public ResponseEntity<ApiResponseDto<?>> createPost(@RequestBody PostRequestDto postRequestDto) {
        log.info("Creating new post with details: {}", postRequestDto);
        return postService.createNewPost(postRequestDto);
    }

    @Operation(summary = "Delete Post")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponseDto<?>> deletePost(@PathVariable Integer postId) throws Exception {
        log.info("Deleting post with ID: {}", postId);
        return postService.deletePostbyId(postId);
    }

    @Operation(summary = "Retrieve Post by ID")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponseDto<?>> findPostById(@PathVariable Integer postId) throws Exception {
        log.info("Fetching post with ID: {}", postId);
        return postService.findPostById(postId);
    }

    @Operation(summary = "Retrieve All Posts by User ID")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponseDto<?>> findUsersPost(@PathVariable Integer userId) {
        log.info("Fetching posts for user ID: {}", userId);
        return postService.findAllPostByUserId(userId);
    }

    @Operation(summary = "Retrieve All Posts")
    @GetMapping
    public ResponseEntity<ApiResponseDto<?>> findAllPost() {
        log.info("Fetching all posts");
        return postService.findAllPost();
    }



    @Operation(summary = "Like or Unlike Post")
    @PutMapping("/like/{postId}")
    public ResponseEntity<ApiResponseDto<?>> likePostHandler(@PathVariable Integer postId) throws Exception {
        log.info("Liking/Unliking post with ID: {}", postId);
        return postService.likePost(postId);
    }
}
